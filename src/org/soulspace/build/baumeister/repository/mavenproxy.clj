(ns org.soulspace.build.baumeister.repository.mavenproxy
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function net]
        [org.soulspace.clj.version version]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository protocol]
        [org.soulspace.build.baumeister.utils log xml]
        [org.soulspace.build.baumeister.maven maven-utils metadata-model]))

;
; repository metadata handling
;
;
; Maven Proxy Artifact Repository
;
(defrecord MavenProxyArtifactRepositoryImpl [usage url path]
  ArtifactRepository
  (artifact-folder [repo artifact]
    (if (seq (artifact-version artifact))
      (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (artifact-version artifact))
      (str (ns-to-path (:project artifact)) "/" (:module artifact))))
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
  (versions [repo artifact]
    (map new-version (:versions (:versioning (get-metadata repo artifact)))))
    ; (map new-version (map file-name (files (module-dir repo artifact)))))
  (latest? [repo artifact]
    (same-version? (:version artifact) (latest-version repo artifact)))
  (latest-version [repo artifact]
    (let [metadata (get-metadata repo artifact)
          latest (new-version (:latest (:versioning metadata)))]
      (new-version latest)))
  (latest-artifact [repo artifact]
    (new-artifact-version artifact (latest-version repo artifact)))
  (find-artifact [repo artifact]
    (if (seq (artifact-version artifact))
      (get-artifact repo artifact)
      (get-artifact repo (latest-artifact repo artifact))))

  MavenArtifactRepository
  (maven-name [repo artifact]
    (if (seq (artifact-version artifact))
      (str (:name artifact) "-" (artifact-version artifact) "." (:type artifact))
      (str (:name artifact) "." (:type artifact))))
  (pom-artifact [repo artifact]
    (new-artifact [(:project artifact) (:module artifact) (artifact-version artifact) (:module artifact) "pom"]))
  (get-pom [repo artifact]
    (let [pom-file (find-artifact repo (pom-artifact repo artifact))]
      (if (exists? pom-file)
        (let [zipped (xml-zipper pom-file)]
          (if (pom-parent? zipped)
            (let [parent-artifact (new-artifact (parse-pom-parent {} zipped))
                  parent-pom (get-pom repo parent-artifact)]
              (parse-pom zipped parent-pom))
            (parse-pom zipped)))
        nil)))
  (metadata-folder [repo artifact]
    (str (ns-to-path (:project artifact) "/" (:module artifact))))
  (get-metadata [repo artifact]
    (let [md-artifact (new-artifact [(:project artifact) (:module artifact) "" "maven-metadata" "xml"])
          md-file (get-artifact repo md-artifact)]
      (println "MD-File" md-file)
      (if (exists? md-file)
        (parse-metadata (xml-zipper md-file)))))

  FileArtifactRepository
  (project-dir [repo artifact]
    (as-file (str  path "/" (ns-to-path (:project artifact)))))
  (module-dir [repo artifact]
    (as-file (str  path "/" (ns-to-path (:project artifact)) "/" (:module artifact))))
  (artifact-dir [repo artifact]
    (as-file (str  path "/" (artifact-folder repo artifact))))
  (artifact-file [repo artifact]
    ;(log :trace (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-mvn-name repo artifact)))
    (as-file (str (absolute-path (artifact-dir repo artifact)) "/" (maven-name repo artifact))))
  
  HttpArtifactRepository
  (project-dir-url [repo artifact]
    (as-url (str url "/" (ns-to-path (:project artifact)))))
  (module-dir-url [repo artifact]
    (as-url (str url "/" (ns-to-path (:project artifact)) "/" (:module artifact))))
  (artifact-dir-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact))))
  (artifact-file-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact) "/" (maven-name repo artifact))))
  
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
