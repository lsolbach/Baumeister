;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.dependency
    (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.clj.artifact artifact]
        [baumeister.config registry]
        [baumeister.repository protocol repositories]
        [baumeister.dependency dependency dependency-transitivity dependency-initialization dependency-dot]
        [baumeister.utils ant-utils checks log]))

(defn dependency-generate-dot
  "Generate dot graphs for the configured dependency tree."
  []
  (with-open [wrt (java.io.StringWriter.)]
    (dependencies-dot wrt (resolve-module-dependency-tree))
    (spit (param "${deps-report-dir}/dependencies.dot") (str wrt))
    ;(execute "dot" (str "-Tpng -o" (param "${deps-report-dir}/dependencies.png") " " (param "${deps-report-dir}/dependencies.dot")))
    ))

; TODO move to distribution plugin
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
(defn dependency-clean []
  (message :fine "cleaning dependencies...")
  (delete-dir (as-file (param :deps-report-dir))))

(defn dependency-init []
  (message :fine "initializing dependencies...")
  (create-dir (as-file (param :lib-runtime-dir)))
  (create-dir (as-file (param :lib-dev-dir)))
  (create-dir (as-file (param :lib-aspect-dir)))
  (create-dir (as-file (param :lib-aspectin-dir)))
  (create-dir (as-file (param :lib-model-dir)))
  (create-dir (as-file (param :lib-generator-dir)))
  (create-dir (as-file (param :lib-data-dir)))
  (create-dir (as-file (param :deps-report-dir))))

(defn dependency-dependencies []
  (message :fine "resolving dependencies...")
  ; initialize dependencies
  (let [dependencies (module-dependencies)]
    (register-val :dependencies-processed dependencies)
    (doseq [dependency dependencies]
      (init-dependency dependency))))

(defn dependency-post-dependencies 
  "Post dependencies step."
  []
  (when (param :dependency-report)
    (dependency-generate-dot)))

; TODO move to separate plugin (distribution )
(defn dependency-distribute
  "Distribute generated artifacts to the dev repository."
  []
  (message :fine "distributing artifacts...")
  ; TODO select the correct repository (dev/release)
  (let [repository (get-dev-repository (param :deps-repositories))]
    ; always distribute module.clj
    (distribute-artifact repository (new-artifact [(param :project) (param :module) (param :version) "module" "clj"]) (param :module-dir))
    (when (code-module?)
      (distribute-jars repository))
    (when (web-module?)
      ) ; TODO distribute war files here or use publish?!?
    (when (data-module?)
      (distribute-artifact repository
                           (new-artifact [(param :project) (param :module) (param :version) (param :module) "zip"])))
    (when (plugin? "mdsd")
      (distribute-artifact repository
                           (new-artifact [(param :project) (param :module) (param :version) (param :mdsd-model-name) "xmi"]) (param :mdsd-model-dir)))))
  
(def config
  {:params [[:lib-runtime-dir "${lib-dir}/runtime"]
            [:lib-dev-dir "${lib-dir}/dev"]
            [:lib-aspect-dir "${lib-dir}/aspect"]
            [:lib-aspectin-dir "${lib-dir}/aspectin"]
            [:lib-model-dir "${lib-dir}/model"]
            [:lib-generator-dir "${lib-dir}/generator"]
            [:lib-data-dir "${build.dir}"]
            [:deps-report true]
            [:deps-transitive false]
            [:deps-report-dir "${build-report-dir}/dependencies"]]
   :steps [[:clean dependency-clean]
           [:init dependency-init]
           [:dependencies dependency-dependencies]
           [:post-dependencies dependency-post-dependencies]
           [:distribute dependency-distribute]]
   :functions []})
