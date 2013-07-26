(ns org.soulspace.build.baumeister.repository.maven-proxy-repository
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function net]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repository-protocol artifact version]
        [org.soulspace.build.baumeister.utils log]
        [org.soulspace.build.baumeister.maven maven-utils]))

(defrecord MavenProxyArtifactRepositoryImpl [usage url path]
  ArtifactRepository
  (artifact-folder [repo artifact]
    (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (artifact-version artifact)))

  (get-artifact [repo artifact]
    (when (and (not (local-hit? repo artifact)) (remote-hit? repo artifact))
      ; local miss but remote hit, cache from remote
      (cache-artifact repo artifact))
    (if (local-hit? repo artifact)
      (artifact-file repo artifact)
      nil))
  
  (get-dependencies-for-artifact [repo artifact]
    (pom-dependencies-data (get-pom repo (pom-artifact repo artifact))))

  (put-artifact [repo artifact artifact-src]
    (create-dir (artifact-dir repo artifact))
    (copy artifact-src (artifact-file repo artifact))) ; TODO synchronize with remote?
  
  VersionedArtifactRepository
  (get-versions-for-artifact [repo artifact] ; TODO queries local filesystem, use module-dir-url and HTTP lookup?
    (map new-version (map file-name (list-files (module-dir repo artifact)))))

  MavenArtifactRepository
  (artifact-mvn-name [repo artifact]
    (str (:name artifact) "-" (artifact-version artifact) "." (:type artifact)))

  (pom-artifact [repo artifact]
    (new-artifact [(:project artifact) (:module artifact) (artifact-version artifact) (:module artifact) "pom"]))

  (get-pom [repo artifact]
    (let [pom-file (get-artifact repo (pom-artifact repo artifact))]
      (if (exists? pom-file)
        (let [zipped (pom-zipper pom-file)]
          (if (pom-parent? zipped)
            (let [parent-artifact (new-artifact (parse-pom-parent {} zipped))
                  parent-pom (get-pom repo parent-artifact)]
              (parse-pom zipped parent-pom))
            (parse-pom zipped)))
        nil)))
  
  FileArtifactRepository
  (project-dir [repo artifact]
    (as-file (str  path "/" (ns-to-path (:project artifact)))))

  (module-dir [repo artifact]
    (as-file (str  path "/" (ns-to-path (:project artifact)) "/" (:module artifact))))

  (artifact-dir [repo artifact]
    (as-file (str  path "/" (artifact-folder repo artifact))))

  (artifact-file [repo artifact]
    ;(log :trace (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-mvn-name repo artifact)))
    (as-file (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-mvn-name repo artifact))))
  
  HttpArtifactRepository
  (project-dir-url [repo artifact]
    (as-url (str url "/" (ns-to-path (:project artifact)))))

  (module-dir-url [repo artifact]
    (as-url (str url "/" (ns-to-path (:project artifact)) "/" (:module artifact))))

  (artifact-dir-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact))))

  (artifact-file-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact) "/" (artifact-mvn-name repo artifact))))
  
  ProxyArtifactRepository
  (cache-artifact [repo artifact]
    (create-dir (artifact-dir repo artifact))
    (copy (input-stream (artifact-file-url repo artifact)) (artifact-file repo artifact)))

  (local-hit? [repo artifact]
    (log :trace "checking local hit for" (artifact-file repo artifact) "->" (exists? (artifact-file repo artifact))) 
    (exists? (artifact-file repo artifact)))

  (remote-hit? [repo artifact]
    (log :trace "checking remote hit for" (artifact-file-url repo artifact))
    (test-url (artifact-file-url repo artifact)))
  )

(defmethod create-repository :maven-proxy [opts]
  (let [[_ usage url path] opts] (MavenProxyArtifactRepositoryImpl. usage url (param path))))

