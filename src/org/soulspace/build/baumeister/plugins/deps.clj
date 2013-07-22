(ns org.soulspace.build.baumeister.plugins.deps
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.dependency dependency dependency-tree dependency-processing dependency-dot]
        [org.soulspace.build.baumeister.utils checks log ant-utils maven-utils]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository artifact repositories distribution]))

;
; workflow functions
;
(defn deps-clean []
  (log :info "cleaning dependencies...")
  (delete-dir (as-file (param :deps-report-dir))))

(defn deps-init []
  (log :info "initializing dependencies...")
  (create-dir (as-file (param :deps-report-dir))))

(defn deps-dependencies []
  (log :info "initializing dependencies...")
  ; initialize dependencies the old way first to leave the project in a usable state for debugging 
  (let [root-dependency (new-dependency (new-artifact (param :project) (param :module) (param :version)) "root")
        [dependency-root loaded-set] (build-dependency-tree root-dependency #{} #{})
        [q in ex] (process-dependency-tree (:dependencies dependency-root) {} #{} #{})]
    (log :trace "DEPS" dependency-root)
    (log :trace "Excluded:" ex)
    (log :trace "Included:" in)
    (if (param :deps-report)
      (let [writer (java.io.StringWriter.)]
        (dependencies-dot writer dependency-root)
        (spit (param "${deps-report-dir}/dependencies.dot") (str writer))))))

(defn deps-distribute []
  (log :info "distributing artifacts...")
  (when (code-module?)
    (deps-distribute-jars (get-dev-repository (param :deps-repositories))))
  (when (web-module?)
    ) ; TODO distribute war files here or use publish?!?
  (when (data-module?)
    (distribute-artifact (get-dev-repository (param :deps-repositories))
                         (new-artifact (param :project) (param :module) (param :version) (param :module) "zip")))
  (when (plugin? "mdsd")
    (distribute-artifact (get-dev-repository (param :deps-repositories))
                         (new-artifact (param :project) (param :module) (param :version) (param :mdsd-model-name) "xmi") (param :mdsd-model-dir))))

;
; plugin initialization
;
(defn plugin-init []
  (log :info "initializing plugin deps")
  (log :debug "creating repositories " (create-repositories (param :repositories)))
  (register-plugin "deps")
  (register-val :deps-repositories (create-repositories (param :repositories)))
  (register-vars [[:deps-report true]
                  [:deps-transitive false]
                  [:deps-report-dir "${build-report-dir}/dependencies"]])
  (register-fns [[:clean deps-clean]
                 [:init deps-init]
                 [:dependencies deps-dependencies]
                 [:distribute deps-distribute]]))
