(ns org.soulspace.build.baumeister.plugins.deps
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.dependency artifact dependency dependency-tree dependency-processing dependency-dot]
        [org.soulspace.build.baumeister.utils checks log ant-utils maven-utils]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories]))

; TODO artifact or dependency
(def artifacts [])
(def artifact-root)

; TODO switch to repository abstraction (for repository types?!)
(def repository-path "/home/soulman/devel/repositories/") 
;(def repositories (create-repositories (param :repositories))) ; fine, this creates the configured repositories, TODO: use deps-repositories parameter
(def dev-repository-path (str repository-path "/development")) ; TODO remove, use repository(ies?) with (type repo "development")

;
; distribution of the built artifacts
;
(defn distribute-artifact
  "distribute the artifact into the given repository"
  ([repository artifact]
    (distribute-artifact repository artifact (param :dist-dir)))
  ([repository artifact src-dir]
    (let [artifact-src (as-file (str src-dir "/" (:artifact artifact) "." (:type artifact)))
          artifact-tgt (as-file (str repository "/" (artifact-path artifact)))
          artifact-dir (as-file (str repository "/" (ns-to-path (:project artifact)) "/" (:module artifact) "/" (:version artifact)))]
      (log :debug (str "copying artifact to " artifact-tgt))
      (create-dir artifact-dir)
      (copy artifact-src artifact-tgt))))

(defn deps-distribute-jars []
  "distribute the built jar artifacts"
  (distribute-artifact dev-repository-path (new-artifact (param :project) (param :name) (param :version) "runtime" "module" "clj") (param :module-dir))
  (distribute-artifact dev-repository-path (new-artifact (param :project) (param :name) (param :version) "runtime" (param :name) "jar"))
  (when (unittest?)
    (distribute-artifact dev-repository-path (new-artifact (param :project) (param :name) (param :version) "runtime" (str (param :name) "Unittest") "jar")))
  (when (integrationtest?)
    (distribute-artifact dev-repository-path (new-artifact (param :project) (param :name) (param :version) "runtime" (str (param :name) "Integrationtest") "jar")))
  (when (acceptancetest?)
    (distribute-artifact dev-repository-path (new-artifact (param :project) (param :name) (param :version) "runtime" (str (param :name) "Acceptancetest") "jar")))
  (when (seq (param :package-additional-jars)) ; FIXME resolve dependency on package parameter
    (doseq [[_ artifact-suffix] (param :package-additional-jars)]
      (distribute-artifact dev-repository-path (new-artifact (param :project) (param :name) (param :version) "runtime" (str (param :name) artifact-suffix) "jar"))))
  )

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
  (let [root-dependency (new-dependency (new-artifact (param :project) (param :name) (param :version)) "root")
        [dependency-root loaded-set] (build-dependency-tree root-dependency #{} #{})
        [q in ex] (process-dependency-tree (:dependencies dependency-root) {} #{} #{})]
    (log :trace "DEPS" dependency-root)
    (log :trace "Excluded:" ex)
    (log :trace "Included:" in)
    ;(process-artifacts in) ; process included artifacts
    ; TODO old way, remove when transitive dependencies work
    (process-dependencies (param :dependencies)) ; FIXME build a tree with only direct dependencies and use it for processing
    (if (param :deps-report)
      (let [writer (java.io.StringWriter.)]
        (dependencies-dot writer dependency-root)
        (spit (param "${deps-report-dir}/dependencies.dot") (str writer))))))

(defn deps-distribute []
  (log :info "distributing artifacts...")
  (when (code-module?)
    (deps-distribute-jars))
  (when (web-module?)
    ) ; TODO distribute war files here or use publish?!?
  (when (data-module?)
    (distribute-artifact dev-repository-path
                         (new-artifact (param :project) (param :name) (param :version) (param :name) "zip")))
  (when (plugin? "mdsd")
    (distribute-artifact dev-repository-path
                         (new-artifact (param :project) (param :name) (param :version) (param :mdsd-model-name) "xmi") (param :mdsd-model-dir))))

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
