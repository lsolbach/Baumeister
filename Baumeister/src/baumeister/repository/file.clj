;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.repository.file
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search namespace]
        [org.soulspace.clj.version version]
        [org.soulspace.clj.artifact artifact]
        [baumeister.config registry]
        [baumeister.repository protocol]
        [baumeister.utils log]))
;
; Repository implementations
;
(defrecord FileArtifactRepositoryImpl [usage path]
;  "A file artifact repository in Baumeister layout."

  ArtifactRepository
  (artifact-folder [repo artifact]
    (str (ns-to-path (:project artifact)) "/" (:module artifact) "/" (artifact-version artifact)))
  (get-artifact [repo artifact]
    (artifact-file repo artifact))
  (get-dependencies-for-artifact [repo artifact]
    (let [module-file (find-artifact repo (module-artifact repo artifact))]
      (if (exists? module-file)
        (:dependencies (apply hash-map (load-string (slurp module-file))))
        nil)))
  (put-artifact [repo artifact artifact-src]
    (log :trace "putting" artifact-src "to" (artifact-file repo artifact))
    (create-dir (artifact-dir repo artifact))
    (copy artifact-src (artifact-file repo artifact)))

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
  )
