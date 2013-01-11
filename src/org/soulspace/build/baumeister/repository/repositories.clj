(ns org.soulspace.build.baumeister.repository.repositories
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj.lib file file-search function net]
        [org.soulspace.build.baumeister.utils artifact maven-utils log]
        [org.soulspace.build.baumeister.config registry]))

(defn artifact-prefix [artifact]
  (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (:version artifact)))

(defn artifact-path [artifact]
  (str (artifact-prefix artifact) "/" (:artifact artifact) "." (:type artifact)))  

(defn module-artifact [artifact]
  (new-artifact [(:project artifact) (:module artifact) (:version artifact) "dependency" "module" "clj"]))

(defn mvn-artifact-path [artifact]
  (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (:version artifact) "/" (:artifact artifact) "-" (:version artifact) "." (:type artifact)))  

(defn mvn-pom-artifact [artifact]
  (new-artifact [(:project artifact) (:module artifact) (:version artifact) "dependency" (:module artifact) "pom"]))

; TODO add usage type of repository ("dev" "release" "thirdparty"), add (usage-type [repo] "Returns the usage type of the repository") 
(defprotocol ArtifactRepository
  "Protocol for an artifact repository"
  (artifact [repo artifact] "Get an artifact from the artifact repository")
;  (module-definition [repo artifact] "Check for dependencies")
  (dependencies [repo artifact] "Get the dependencies for an artifact from the artifact repository")
  (put-artifact [repo artifact data] "Put an artifact into the artifact repository"))

(defprotocol FileRepository
  "Protocol for a file based repository"
  (artifact-file [repo artifact] "Get the file for the artifact in this repository"))

(defprotocol HttpRepository
  "Protocol for a http based repository"
  (artifact-url [repo artifact] "Get the url for the artifact in this repository"))

(defprotocol ProxyRepository
  "Protocol for a proxy/cache repository"
  (local-hit? [repo artifact] "Test for a local hit for this artifact")
  (remote-hit? [repo artifact] "Test for a remote hit for this artifact")
  (cache-artifact [repo artifact] "Copies the artifact from the remote site to the local cache"))

(defrecord FileArtifactRepository [usage path]
  ArtifactRepository
  (artifact [repo a] (artifact-file repo a))
  (dependencies [repo a]
                (let [module-file (artifact repo (module-artifact a))]
                  (if (exists? module-file)
                    (:dependencies (apply hash-map (load-string (slurp module-file))))
                    nil)))
  (put-artifact [repo a data]
                (create-dir (as-file (str path "/" (artifact-prefix a))))) ; TODO copy artifact
  FileRepository
  (artifact-file [repo a]
                (as-file (str path "/" (artifact-path a)))))

(defrecord HttpProxyArtifactRepository [usage url path]
  ArtifactRepository
  (artifact [repo a]
            (when (and (not (local-hit? repo a)) (remote-hit? repo a))
              (cache-artifact repo a))
            (if (local-hit? repo a)
              (artifact-file repo a)
              nil))
  (dependencies [repo a]
                (let [module-file (artifact repo (module-artifact a))]
                  (if (exists? module-file)
                    (:dependencies (apply hash-map (load-string (slurp module-file))))
                    nil)))
  (put-artifact [repo a data]
                (create-dir (as-file (str path "/" (artifact-prefix a))))) ; TODO copy artifact, synchronize with remote?
  FileRepository
  (artifact-file [repo a]
                (as-file (str path "/" (artifact-path a))))
  HttpRepository
  (artifact-url [repo a]
                (as-url (str url "/" (artifact-path a))))
  ProxyRepository
  (cache-artifact [repo a]
                  (create-dir (artifact-prefix a))
                  (copy (input-stream (artifact-url repo a)) (artifact-file repo a)))
  (local-hit? [repo a]
              (log :debug "checking local hit for" (artifact-file repo a) "->" (exists? (artifact-file repo a))) 
              (exists? (artifact-file repo a)))
  (remote-hit? [repo a]
               (log :debug "checking remote hit for" (artifact-url repo a))
               (test-url (artifact-url repo a))))

(defrecord MavenProxyArtifactRepository [usage url path]
  ArtifactRepository
  (artifact [repo a]
            (when (and (not (local-hit? repo a)) (remote-hit? repo a))
              (cache-artifact repo a))
            (if (local-hit? repo a)
              (artifact-file repo a)
              nil))
  (dependencies [repo a]
                (let [pom-file (artifact repo (mvn-pom-artifact a))]
                  (if (exists? pom-file)
                    (pom-dependencies pom-file)
                    nil)))
  (put-artifact [repo a data]
                (create-dir (as-file (str path "/" (artifact-prefix a))))) ; TODO copy artifact, synchronize with remote?
  FileRepository
  (artifact-file [repo a]
                (as-file (str path "/" (artifact-path a))))
  HttpRepository
  (artifact-url [repo a]
                (as-url (str url "/" (mvn-artifact-path a))))
  ProxyRepository
  (cache-artifact [repo a]
                  (create-dir (as-file (str path "/" (artifact-prefix a))))
                  (copy (input-stream (artifact-url repo a)) (artifact-file repo a)))
  (local-hit? [repo a]
              (log :debug "checking local hit for" (artifact-file repo a) "->" (exists? (artifact-file repo a))) 
              (exists? (artifact-file repo a)))
  (remote-hit? [repo a]
               (log :debug "checking remote hit for" (artifact-url repo a))
               (test-url (artifact-url repo a))))

(defmulti create-repository first)
(defmethod create-repository :file [opts]
  (let [[_ usage path] opts] (FileArtifactRepository. usage (param path))))

(defmethod create-repository :http-proxy [opts]
  (let [[_ usage path url] opts] (HttpProxyArtifactRepository. usage url (param path))))

(defmethod create-repository :maven-proxy [opts]
  (let [[_ usage url path] opts] (MavenProxyArtifactRepository. usage url (param path))))

(defn create-repositories [v]
  (map create-repository v))

(defn query-artifact [repositories a]
  (if (seq repositories)
    ; Iterate through the configured repositories to find this artifact
    (let [artifact-file (artifact (first repositories) a)]
      (log :trace "Querying repository" (first repositories))
      (if (exists? artifact-file)
        (do 
          (log :debug "Found artifact" (artifact-name-version a) "in the configured repositories!")
          artifact-file)
        (recur (rest repositories) a)))
    (do
      (log :debug "Could not find artifact" (artifact-name-version a) "in the configured repositories!")
      nil)))

(defn query-dependencies [repositories a]
  (if (seq repositories)
    ; Iterate through the configured repositories to find the dependencies file for this artifact
    (let [deps (dependencies (first repositories) a)]
      (log :trace "Querying depependencies from repository" (first repositories))
      ; test for nil instead of seq to distinguish between no module/pom file or no dependencies in module/pom file
      (if-not (nil? deps)
        (do 
          (log :debug "Found" (count deps) "dependencies for artifact" (artifact-name-version a) "in the configured repositories!")
          deps)
        (recur (rest repositories) a)))
    (do (log :debug "No dependencies found for artifact" (artifact-name-version a) "in the configured repositories!")
      nil)))
