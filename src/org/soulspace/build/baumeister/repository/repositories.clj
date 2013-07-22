(ns org.soulspace.build.baumeister.repository.repositories
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function net]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.utils maven-utils log]
        [org.soulspace.build.baumeister.config registry]))

;
; Repository protocols
;

; TODO add usage type of repository ("dev" "release" "thirdparty"), add (usage-type [repo] "Returns the usage type of the repository") 
(defprotocol ArtifactRepository
  "Protocol for an artifact repository"
  (artifact-folder [repo artifact] "Get the folder of this artifact in this repository")
  (get-artifact [repo artifact] "Get an artifact from the artifact repository")
;  (module-definition [repo artifact] "Check for dependencies")
  (get-dependencies-for-artifact [repo artifact] "Get the dependencies for an artifact from the artifact repository")
  (put-artifact [repo artifact artifact-src] "Put an artifact-src as an artifact into the artifact repository"))

(defprotocol FileArtifactRepository
  "Protocol for a file based repository"
  (artifact-dir [repo artifact] "Get the directory of this artifact in this repository")
  (artifact-file [repo artifact] "Get the file for the artifact in this repository"))

(defprotocol HttpArtifactRepository
  "Protocol for a http based repository"
  (artifact-url [repo artifact] "Get the url for the artifact in this repository"))

(defprotocol ProxyArtifactRepository
  "Protocol for a proxy/cache repository"
  (local-hit? [repo artifact] "Test for a local hit for this artifact")
  (remote-hit? [repo artifact] "Test for a remote hit for this artifact")
  (cache-artifact [repo artifact] "Copies the artifact from the remote site to the local cache"))

(defprotocol BaumeisterArtifactRepository
  (module-artifact [repo artifact] "Get the module artifact for this artifact"))

(defprotocol MavenArtifactRepository
  "Protocol for a maven2 artifact repository"
  (artifact-mvn-name [repo artifact] "Get the maven name of this artifact")
  (pom-artifact [repo artifact] "Get the maven pom artifact for this artifact"))

;
; Repository implementations
;

(defrecord FileArtifactRepositoryImpl [usage path]
;  "A file artifact repository in Baumeister layout."

  ArtifactRepository
  (artifact-folder [repo artifact]
    (log :info "artifact-folder" (artifact-module-version-key artifact))
    (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (:version artifact)))
  (get-artifact [repo artifact]
    (artifact-file repo artifact))
  (get-dependencies-for-artifact [repo artifact]
    (let [module-file (get-artifact repo (module-artifact repo artifact))]
      (if (exists? module-file)
        (:dependencies (apply hash-map (load-string (slurp module-file))))
        nil)))
  (put-artifact [repo artifact artifact-src]
    (create-dir (artifact-dir repo artifact))
    (copy artifact-src (artifact-file repo artifact))) ; TODO copy artifact

  BaumeisterArtifactRepository
  (module-artifact [repo artifact]
    (new-artifact [(:project artifact) (:module artifact) (:version artifact) "module" "clj"]))

  FileArtifactRepository
  (artifact-dir [repo artifact]
    (as-file (str  path "/" (artifact-folder repo artifact))))
  (artifact-file [repo artifact]
    (log :info (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-name artifact)))
    (as-file (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-name artifact))))
  )

(defrecord HttpProxyArtifactRepositoryImpl [usage url path]
;  "A proxy artifact repository in Baumeister layout that can retrieve and store artifacts from an HTTP repository."
  ArtifactRepository
  (artifact-folder [repo artifact]
    (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (:version artifact)))
  (get-artifact [repo  artifact]
    (when (and (not (local-hit? repo artifact)) (remote-hit? repo artifact))
      (cache-artifact repo artifact))
    (if (local-hit? repo artifact)
      (artifact-file repo artifact)
      nil))
  (get-dependencies-for-artifact [repo artifact]
    (let [module-file (get-artifact repo (module-artifact repo artifact))]
      (if (exists? module-file)
        (:dependencies (apply hash-map (load-string (slurp module-file))))
        nil)))
  (put-artifact [repo artifact artifact-src]
    (create-dir (artifact-dir repo artifact))
    (copy artifact-src (artifact-file repo artifact))) ; TODO synchronize with remote?

  BaumeisterArtifactRepository
  (module-artifact [repo artifact]
    (new-artifact [(:project artifact) (:module artifact) (:version artifact) "module" "clj"]))

  FileArtifactRepository
  (artifact-dir [repo artifact]
    (as-file (str  path "/" (artifact-folder repo artifact))))
  (artifact-file [repo artifact]
    (as-file (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-name artifact))))

  HttpArtifactRepository
  (artifact-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact) "/" (artifact-name artifact))))

  ProxyArtifactRepository
  (cache-artifact [repo artifact]
    (create-dir (artifact-dir repo artifact))
    (copy (input-stream (artifact-url repo artifact)) (artifact-file repo artifact)))
  (local-hit? [repo artifact]
    (log :debug "checking local hit for" (artifact-file repo artifact) "->" (exists? (artifact-file repo artifact))) 
    (exists? (artifact-file repo artifact)))
  (remote-hit? [repo artifact]
    (log :debug "checking remote hit for" (artifact-url repo artifact))
    (test-url (artifact-url repo artifact)))
  )


(defrecord MavenProxyArtifactRepositoryImpl [usage url path]
  ArtifactRepository
  (artifact-folder [repo artifact]
    (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (:version artifact)))
  (get-artifact [repo artifact]
    (when (and (not (local-hit? repo artifact)) (remote-hit? repo artifact))
      (cache-artifact repo artifact))
    (if (local-hit? repo artifact)
      (artifact-file repo artifact)
      nil))
  (get-dependencies-for-artifact [repo artifact]
    (let [pom-file (get-artifact repo (pom-artifact repo artifact))]
      (if (exists? pom-file)
        (pom-dependencies pom-file)
        nil)))
  (put-artifact [repo artifact artifact-src]
    (create-dir (artifact-dir repo artifact))
    (copy artifact-src (artifact-file repo artifact))) ; TODO synchronize with remote?
  
  MavenArtifactRepository
  (artifact-mvn-name [repo artifact]
    (str (:name artifact) "-" (:version artifact) "." (:type artifact)))
  (pom-artifact [repo artifact]
    (new-artifact [(:project artifact) (:module artifact) (:version artifact) (:module artifact) "pom"]))
  
  FileArtifactRepository
  (artifact-dir [repo artifact]
    (as-file (str  path "/" (artifact-folder repo artifact))))
  (artifact-file [repo artifact]
    ;(log :trace (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-mvn-name repo artifact)))
    (as-file (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-mvn-name repo artifact))))
  
  HttpArtifactRepository
  (artifact-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact) "/" (artifact-mvn-name repo artifact))))
  
  ProxyArtifactRepository
  (cache-artifact [repo artifact]
    (create-dir (artifact-dir repo artifact))
    (copy (input-stream (artifact-url repo artifact)) (artifact-file repo artifact)))
  (local-hit? [repo artifact]
    (log :debug "checking local hit for" (artifact-file repo artifact) "->" (exists? (artifact-file repo artifact))) 
    (exists? (artifact-file repo artifact)))
  (remote-hit? [repo artifact]
    (log :debug "checking remote hit for" (artifact-url repo artifact))
    (test-url (artifact-url repo artifact)))
  )

;
;
;
(defmulti create-repository first)
(defmethod create-repository :file [opts]
  (let [[_ usage path] opts] (FileArtifactRepositoryImpl. usage (param path))))

(defmethod create-repository :http-proxy [opts]
  (let [[_ usage url path] opts] (HttpProxyArtifactRepositoryImpl. usage url (param path))))

(defmethod create-repository :maven-proxy [opts]
  (let [[_ usage url path] opts] (MavenProxyArtifactRepositoryImpl. usage url (param path))))

(defn create-repositories [v]
  (map create-repository v))

(defn get-dev-repository [repositories]
  (first (filter #(= (:usage %) :development) repositories)))

(defn get-release-repository [repositories]
  (first (filter #(= (:usage %) :release) repositories)))

(defn repositories-by-usage [repositories usage]
  (filter #(= (:usage %) (keyword usage)) repositories))

;
;
;
(defn query-artifact [repositories artifact]
  (log :debug "querying artifact:" (artifact-name-version artifact))
  (if (seq repositories)
    ; Iterate through the configured repositories to find this artifact
    (let [repo (first repositories)
          artifact-file (get-artifact repo artifact)]
      (log :trace "querying repository" repo)
      (if (exists? artifact-file)
        (do 
          (log :info "Found artifact" (artifact-name-version artifact) "in the configured repositories!")
          artifact-file)
        (recur (rest repositories) artifact)))
    (do
      (log :info "Could not find artifact" (artifact-name-version artifact) "in the configured repositories!")
      nil)))

(defn query-dependencies [repositories dependency]
  (log :debug "querying dependencies:" dependency)
  (log :debug "querying dependencies in artifact:" (:artifact dependency))
  (if (seq repositories)
    ; Iterate through the configured repositories to find the dependencies file for this artifact
    (let [repo (first repositories)
          deps (get-dependencies-for-artifact repo (:artifact dependency))]
      (log :trace "Querying depependencies from repository" repo)
      ; test for nil instead of seq to distinguish between no module/pom file or no dependencies in module/pom file
      (if-not (nil? deps)
        (do 
          (log :debug "Found" (count deps) "dependencies for artifact" (artifact-name-version (:artifact dependency)) "in the configured repositories!")
          deps)
        (recur (rest repositories) (:artifact dependency))))
    (do (log :debug "No dependencies found for artifact" (artifact-name-version (:artifact dependency)) "in the configured repositories!")
      nil)))
