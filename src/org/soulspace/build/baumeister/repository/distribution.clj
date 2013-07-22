(ns org.soulspace.build.baumeister.repository.distribution
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils checks log]
        [org.soulspace.build.baumeister.repository repositories artifact]
        ))

;
; distribution of the built artifacts
;
(defn distribute-artifact
  "distribute the artifact into the given repository"
  ([repository artifact]
    (distribute-artifact repository artifact (param :dist-dir)))
  ([repository artifact src-dir]
    (let [artifact-src (as-file (str src-dir "/" (artifact-name artifact)))]
      (put-artifact repository artifact artifact-src))))

(defn deps-distribute-jars [repository]
  "distribute the built jar artifacts"
  (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) "module" "clj"]) (param :module-dir))
  (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) (param :module) "jar"]))
  (when (unittest?)
    (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) (str (param :module) "Unittest") "jar"])))
  (when (integrationtest?)
    (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) (str (param :module) "Integrationtest") "jar"])))
  (when (acceptancetest?)
    (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) (str (param :module) "Acceptancetest") "jar"])))
  (when (seq (param :package-additional-jars)) ; FIXME resolve dependency on package parameter
    (doseq [[_ artifact-suffix] (param :package-additional-jars)]
      (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) (str (param :module) artifact-suffix) "jar"])))))
