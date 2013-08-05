;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.plugins.dependencies
    (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry plugin-registry]
        [org.soulspace.build.baumeister.repository protocol repositories]
        [org.soulspace.build.baumeister.dependency dependency dependency-node dependency-initialization dependency-dot]
        [org.soulspace.build.baumeister.utils ant-utils checks log message]))

(defn get-dependencies []
  (if (or (param :dependeny-transitive) (= true (param :dependeny-transitive)))
    (let [root (build-dependency-tree)]
      (register-val :dependencies-tree root)
      (process-tree [root] []))
    (map #(apply new-dependency %) (param :dependencies))))

(defn generate-dot
  "Generate dot graphs for the configured dependency tree."
  [dependency-tree]
  (let [writer (java.io.StringWriter.)]
    (if-not (nil? (param :dependencies-tree))
      (dependencies-dot writer (param :dependencies-tree))
      (dependencies-dot writer (build-dependency-tree)))
    (spit (param "${deps-report-dir}/dependencies.dot") (str writer))
    ;(execute "dot" (str "-Tpng -o" (param "${deps-report-dir}/dependencies.png") " " (param "${deps-report-dir}/dependencies.dot")))
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

(defn distribute-jars [repository]
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

;
; workflow functions
;
(defn dependencies-clean []
  (message :fine "cleaning dependencies...")
  (delete-dir (as-file (param :deps-report-dir))))

(defn dependencies-init []
  (message :fine "initializing dependencies...")
  (create-dir (as-file (param :deps-report-dir)))
  ; TODO move repository initalization into Baumeister repository registry or so
  (register-val :deps-repositories (create-repositories (param :repositories))))

(defn dependencies-dependencies []
  (message :fine "resolving dependencies...")
  ; initialize dependencies
  (let [dependencies (get-dependencies)]
    (register-val :dependencies-processed dependencies)
    (doseq [url (dependency-urls dependencies)] ; Add to classpath some time
      (println url)) 
    (doseq [dependency dependencies]
      (init-dependency dependency))))

(defn dependencies-post-dependencies 
  "Post dependencies step."
  []
  (when (param :deps-report)
    (if-not (nil? (param :dependencies-tree))
      (generate-dot (param :dependencies-tree))
      (generate-dot (build-dependency-tree)))))

(defn dependencies-distribute
  "Distribute generated artifacts to the dev repository."
  []
  (message :fine "distributing artifacts...")
  (when (code-module?)
    (distribute-jars (get-dev-repository (param :deps-repositories))))
  (when (web-module?)
    ) ; TODO distribute war files here or use publish?!?
  (when (data-module?)
    (distribute-artifact (get-dev-repository (param :deps-repositories))
                         (new-artifact [(param :project) (param :module) (param :version) (param :module) "zip"])))
  (when (plugin? "mdsd")
    (distribute-artifact (get-dev-repository (param :deps-repositories))
                         (new-artifact [(param :project) (param :module) (param :version) (param :mdsd-model-name) "xmi"]) (param :mdsd-model-dir))))

(def config
  {:params [[:deps-report true]
            [:deps-transitive false]
            [:deps-report-dir "${build-report-dir}/dependencies"]]
   :functions [[:clean dependencies-clean]
               [:init dependencies-init]
               [:dependencies dependencies-dependencies]
               [:post-dependencies dependencies-post-dependencies]
               [:distribute dependencies-distribute]]})
