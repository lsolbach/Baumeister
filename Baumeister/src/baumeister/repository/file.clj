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
  (:require [clojure.java.io :as io]
            [org.soulspace.clj.file :as sfile]
            [org.soulspace.tools.artifact :as artifact]
            [org.soulspace.tools.version :as version]
            [org.soulspace.clj.namespace :as namespace]
            [baumeister.repository protocol :as protocol]
            [baumeister.utils.log :as log]))
;;;
;;; Local filesystem repository implementation
;;;
(defrecord FileArtifactRepositoryImpl [usage path]
;  "A file artifact repository in Baumeister layout."

  ArtifactRepository
  (artifact-folder [repo artifact]
    (str (namespace/ns-to-path (:project artifact)) "/" (:module artifact) "/" (artifact/artifact-version artifact)))
  (get-artifact [repo artifact]
    (artifact-file repo artifact))
  (get-dependencies-for-artifact [repo artifact]
    (let [module-file (find-artifact repo (module-artifact repo artifact))]
      (if (io/exists? module-file)
        (:dependencies (apply hash-map (load-string (slurp module-file))))
        nil)))
  (put-artifact [repo artifact artifact-src]
    (log/log :trace "putting" artifact-src "to" (artifact-file repo artifact))
    (io/create-dir (artifact-dir repo artifact))
    (io/copy artifact-src (artifact-file repo artifact)))

  VersionedArtifactRepository
  (versions [repo artifact]
    (map version/new-version (map sfile/file-name (sfile/files (module-dir repo artifact)))))
  (latest? [repo artifact]
    (version/same-version? (:version artifact) (latest-version repo artifact)))
  (latest-version [repo artifact]
    (first (reverse (sort version/compare-version (versions repo artifact)))))
  (latest-artifact [repo artifact]
    (artifact/new-artifact-version artifact (latest-version repo artifact)))
  (find-artifact [repo artifact]
    (if (seq (artifact/artifact-version artifact))
      (get-artifact repo artifact)
      (get-artifact repo (latest-artifact repo artifact))))

  BaumeisterArtifactRepository
  (module-artifact [repo artifact]
    (artifact/new-artifact [(:project artifact) (:module artifact) (artifact/artifact-version artifact) "module" "clj"]))

  FileArtifactRepository
  (project-dir [repo artifact]
    (io/as-file (str  path "/" (namespace/ns-to-path (:project artifact)))))
  (module-dir [repo artifact]
    (io/as-file (str  path "/" (namespace/ns-to-path (:project artifact)) "/" (:module artifact))))
  (artifact-dir [repo artifact]
    (io/as-file (str  path "/" (artifact-folder repo artifact))))
  (artifact-file [repo artifact]
    (io/as-file (str (sfile/absolute-path (artifact-dir repo artifact)) "/" (artifact/artifact-name artifact))))
  )
