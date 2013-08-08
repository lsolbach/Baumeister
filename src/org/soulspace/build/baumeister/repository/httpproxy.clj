;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.repository.httpproxy
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search namespace net]
        [org.soulspace.clj.version version]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository protocol]
        [org.soulspace.build.baumeister.utils log]))

(defrecord HttpProxyArtifactRepositoryImpl [usage url path]
;  "A proxy artifact repository in Baumeister layout that can retrieve and store artifacts from an HTTP repository."
  ArtifactRepository
  (artifact-folder [repo artifact]
    (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (artifact-version artifact)))
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

  VersionedArtifactRepository
  (versions [repo artifact]
    (map new-version (map file-name (files (module-dir repo artifact)))))
  (latest? [repo artifact]
    (same-version? (:version artifact) (latest-version repo artifact)))
  (latest-version [repo artifact]
    (first (reverse (sort compare-version (versions repo artifact)))))
  (latest-artifact [repo artifact]
    (new-artifact-version artifact (latest-version repo artifact)))
  (find-artifact [repo artifact]
    (if (seq (artifact-version artifact))
      (get-artifact repo artifact)
      (get-artifact repo (latest-artifact repo artifact))))

  BaumeisterArtifactRepository
  (module-artifact [repo artifact]
    (new-artifact [(:project artifact) (:module artifact) (artifact-version artifact) "module" "clj"]))

  FileArtifactRepository
  (project-dir [repo artifact]
    (as-file (str  path "/" (ns-to-path (:project artifact)))))
  (module-dir [repo artifact]
    (as-file (str  path "/" (ns-to-path (:project artifact)) "/" (:module artifact))))
  (artifact-dir [repo artifact]
    (as-file (str  path "/" (artifact-folder repo artifact))))
  (artifact-file [repo artifact]
    (as-file (str (absolute-path (artifact-dir repo artifact)) "/" (artifact-name artifact))))

  HttpArtifactRepository
  (project-dir-url [repo artifact]
    (as-url (str url "/" (ns-to-path (:project artifact)))))
  (module-dir-url [repo artifact]
    (as-url (str url "/" (ns-to-path (:project artifact)) "/" (:module artifact))))
  (artifact-dir-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact))))
  (artifact-file-url [repo artifact]
    (as-url (str url "/" (artifact-folder repo artifact) "/" (artifact-name artifact))))

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
