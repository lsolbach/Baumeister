(ns org.soulspace.build.baumeister.plugins.sdeps
    (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.config registry plugin-registry]
        [org.soulspace.build.baumeister.repository repositories artifact distribution]
        [org.soulspace.build.baumeister.dependency dependency dependency-node dependency-initialization  dependency-dot]
        [org.soulspace.build.baumeister.utils ant-utils checks log message]))

(defn get-dependencies []
  (if (or (param :dependeny-transitive) (= true (param :dependeny-transitive)))
    (let [root (build-dependency-tree)]
      (register-val :dependencies-tree root)
      (process-tree [root] []))
    (map #(apply new-dependency %) (param :dependencies))))

;
; workflow functions
;
(defn sdeps-clean []
  (message :fine "cleaning dependencies...")
  (delete-dir (as-file (param :deps-report-dir))))

(defn sdeps-init []
  (message :fine "initializing dependencies...")
  (create-dir (as-file (param :deps-report-dir))))

(defn sdeps-dependencies []
  (message :fine "resolving dependencies...")
  ; initialize dependencies
  (let [dependencies (get-dependencies)]
    (register-val :dependencies-processed dependencies)
    (doseq [dependency dependencies]
      (init-dependency dependency))))

(defn sdeps-post-dependencies 
  "Generate dot graphs for the configured dependency tree."
  []
  (when (param :deps-report)
    (log :debug "doing sdeps-post-dependencies")
    (let [writer (java.io.StringWriter.)]
      (if-not (nil? (param :dependencies-tree))
        (dependencies-dot writer (param :dependencies-tree))
        (dependencies-dot writer (build-dependency-tree)))
      (spit (param "${deps-report-dir}/dependencies.dot") (str writer))
      ;(execute "dot" (str "-Tpng -o" (param "${deps-report-dir}/dependencies.png") " " (param "${deps-report-dir}/dependencies.dot")))
      )))

(defn sdeps-distribute
  "Distribute generated artifacts to the dev repository."
  []
  (message :fine "distributing artifacts...")
  (when (code-module?)
    (deps-distribute-jars (get-dev-repository (param :deps-repositories))))
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
   :functions [[:clean sdeps-clean]
                 [:init sdeps-init]
                 [:dependencies sdeps-dependencies]
                 [:post-dependencies sdeps-post-dependencies]
                 [:distribute sdeps-distribute]]})

;
; plugin initialization
;
(defn plugin-init []
  (message :fine "initializing plugin sdeps")
  (log :debug "creating repositories " (param :repositories))
  ; TODO move repository creation into another plugin
  (register-val :deps-repositories (create-repositories (param :repositories)))
  (register-vars (:params config))
  (register-fns (:functions config)))
