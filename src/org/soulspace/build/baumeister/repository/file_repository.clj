(ns org.soulspace.build.baumeister.repository.file-repository
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repository-protocol artifact version]
        [org.soulspace.build.baumeister.utils log]))
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
    (let [module-file (get-artifact repo (module-artifact repo artifact))]
      (if (exists? module-file)
        (:dependencies (apply hash-map (load-string (slurp module-file))))
        nil)))
  (put-artifact [repo artifact artifact-src]
    (create-dir (artifact-dir repo artifact))
    (copy artifact-src (artifact-file repo artifact))) ; TODO copy artifact

  VersionedArtifactRepository
  (get-versions-for-artifact [repo artifact]
    (map new-version (map file-name (list-files (module-dir repo artifact)))))
  
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

(defmethod create-repository :file [opts]
  (let [[_ usage path] opts] (FileArtifactRepositoryImpl. usage (param path))))

