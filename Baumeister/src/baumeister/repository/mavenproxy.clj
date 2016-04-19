;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.repository.mavenproxy
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search namespace net]
        [org.soulspace.clj.xml zip]
        [org.soulspace.clj.version version]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.clj.maven pom-model metadata-model]
        [baumeister.config registry]
        [baumeister.repository protocol]
        [baumeister.utils log]))

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
    (when (local-hit? repo artifact)
      (artifact-file repo artifact)))
  
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
            (parse-pom zipped))))))
  
  (metadata-folder [repo artifact]
    (str (ns-to-path (:project artifact) "/" (:module artifact))))
  
  (get-metadata [repo artifact]
    (log :trace "getting repository metadata for " (:project artifact) "/" (:module artifact))
    (let [md-artifact (new-artifact [(:project artifact) (:module artifact) "" "maven-metadata" "xml"])
          md-file (get-artifact repo md-artifact)]
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
    (let [arti-file (artifact-file repo artifact)]
      (log :trace "caching artefact" arti-file)
      (create-dir (artifact-dir repo artifact))
      (copy (input-stream (artifact-file-url repo artifact)) arti-file)))

  (local-hit? [repo artifact]
    (let [local-hit (exists? (artifact-file repo artifact))]
      (log :trace "checking local hit for" (artifact-file repo artifact) "->" local-hit) 
      local-hit))

  (remote-hit? [repo artifact]
    (let [remote-hit (test-url (artifact-file-url repo artifact))]
      (log :trace "checking remote hit for" (artifact-file-url repo artifact) "->" remote-hit)
      remote-hit)))
