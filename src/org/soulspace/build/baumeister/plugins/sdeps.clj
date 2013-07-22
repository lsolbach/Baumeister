(ns org.soulspace.build.baumeister.plugins.sdeps
    (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories artifact distribution]
        [org.soulspace.build.baumeister.dependency dependency dependency-initialization]
        [org.soulspace.build.baumeister.utils ant-utils checks log]))

;
; workflow functions
;
(defn sdeps-clean []
  (log :info "cleaning dependencies...")
  (delete-dir (as-file (param :deps-report-dir))))

(defn sdeps-init []
  (log :info "initializing dependencies...")
  (create-dir (as-file (param :deps-report-dir))))

(defn sdeps-dependencies []
  (log :info "initializing dependencies...")
  ; initialize dependencies
  (let [dependencies (map #(apply new-dependency %) (param :dependencies))]
    (doseq [dependency dependencies]
      (init-dependency dependency))))

(defn sdeps-distribute []
  (log :info "distributing artifacts...")
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

;
; plugin initialization
;
(defn plugin-init []
  (log :info "initializing plugin deps")
  (register-plugin "deps")
  (log :debug "creating repositories " (param :repositories))
  (register-val :deps-repositories (create-repositories (param :repositories)))
  (register-vars [[:deps-report true]
                  [:deps-transitive false]
                  [:deps-report-dir "${build-report-dir}/dependencies"]])
  (register-fns [[:clean sdeps-clean]
                 [:init sdeps-init]
                 [:dependencies sdeps-dependencies]
                 [:distribute sdeps-distribute]]))
