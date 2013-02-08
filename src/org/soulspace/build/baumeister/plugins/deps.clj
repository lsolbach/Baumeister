(ns org.soulspace.build.baumeister.plugins.deps
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.clj.lib file file-search function]
        [org.soulspace.build.baumeister.utils artifact dependency checks log ant-utils maven-utils]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories]))

; TODO switch to repository abstraction (for repository types?!)

(def artifacts [])
(def artifact-root)

(def repository-path "/home/soulman/devel/repositories/") 
(def repositories (create-repositories (param :repositories))) ; fine, this creates the configured repositories, TODO: use deps-repositories parameter
(def dev-repository-path (str repository-path "/development")) ; TODO remove, use repository(ies?) with (type repo "development")

(defn processed? [dep in ex]
  (let [a (:artifact dep)]
    (or (contains? in a) (contains? ex a))))

(defn add-module [mm dep]
  (let [a (:artifact dep)]
    (assoc mm (artifact-module-key a) a)))

(defn version-conflict? [mm dep]
  (let [a (:artifact dep)
        pa (get mm (artifact-module-key a))]
    (and pa (not (= (get mm (:version (artifact-module-key a))) (:version a))))))

(defn version-conflict [mm dep]
  (let [a (:artifact dep)
        pa (get mm (artifact-module-key a))]
    (log :warn "version conflict for" (artifact-module-key pa) (:version pa) (:version a))))

(defn process-artifact [artifact]
  (let [src (query-artifact (param :deps-repositories) artifact)
        tgt (param (keyword (str "lib-" (:target artifact) "-dir")))]
    (if (nil? src)
      (log :error (artifact-path artifact) "not found in repositories!")
      ; (throw (RuntimeException. (str "Error: " (artifact-path artifact) " not found!")))
      (cond
        (copy? artifact)
        (copy src (as-file (str tgt "/" (:artifact artifact) "." (:type artifact))))
        (unzip? artifact)
        (ant-unzip {:src src :dest tgt :overwrite "true"})))))

(defn process-artifacts [coll]
  (loop [artifacts coll
         missing []]
    (if (seq artifacts)
      (do
        (let [miss (process-artifact (first artifacts))]
          (if (nil? miss)
            (recur (rest artifacts) missing)
            (recur (rest artifacts) (conj missing miss)))))
      (when (seq missing)
        (throw (RuntimeException. (str "Error: " (map artifact-path missing) "could not be found!")))))))
      ;    (doseq [artifact coll]
      ;      (process-artifact artifact)))

; TODO old way, remove when transitive dependencies work
(defn process-dependencies [dependencies]
  "process dependencies as specified in the current module (not transitive)"
  (doseq [dependency dependencies]
    (process-artifact (apply new-artifact dependency))))

; the dependencies of the current node are enqueued to the queue for the preservation of the tree order
; mm is used as module map?!
; in set
; ex set
(defn process-dependency-tree [queue mm in ex]
  "process the nodes of the dependency tree"
  (if-not (seq queue)
    [queue mm in ex] ; queue is empty, return
    (let [dep (first queue)]
      (if (processed? dep in ex) ; check if dep was processed earlier
        (recur (rest queue) mm in ex) ; dep was processed, recur on rest of the queue
        (if (contains? ex dep) ; check if dep was excluded earlier
          (recur (rest queue) mm in ex) ; dep was excluded earlier, recur on rest of the queue
          (if (is-excluded? dep) ; check if dep is excluded
            (recur (rest queue) mm in (conj ex (:artifact dep))) ; dep is excluded here, add to excluded and recur on rest of the queue
            (do
              ; TODO use version matching from artifact
              (when (version-conflict? mm dep) ; check if there's a version conflict on dep
                (version-conflict mm dep))
              (if (seq (:dependencies dep))
                (recur (concat (rest queue) (:dependencies dep)) (add-module  mm dep) (conj in (:artifact dep)) ex)
                (recur (rest queue) (add-module  mm dep) (conj in (:artifact dep)) ex)))))))))

;
; transitive dependencies management
;
(declare dependency-tree)

(defn loaded-artifact [loaded artifact]
  (log :debug "marking artifact loaded" (artifact-key artifact))
  (assoc loaded (artifact-key artifact) artifact))

(defn loaded-artifact? [loaded artifact]
  "tests if an artifact is already loaded"
  (contains? loaded (artifact-key artifact)))

(defn get-dependencies [artifact]
  "get the dependencies configuration for the specified artifact"
  (if (= (:target artifact) "root")
    (param :dependencies) ; current module, use config
    (query-dependencies (param :deps-repositories) artifact)))

; consume artifacts, build nodes
; TODO handle excluded-set
(defn dependency-tree-children [parent artifacts nodes loaded-set excluded-set]
  (if-not (seq artifacts)
    [(new-dependency-node parent nodes) loaded-set] ; build and return dependency node for parent with the subtrees as children
    (let [actual (first artifacts)
          remaining (rest artifacts)
          [node aloaded-set] (dependency-tree actual loaded-set excluded-set) ; build subtree for actual artifact (recursive bottom up because of persistent data structures)
          anodes (if (seq node) (conj nodes node) nodes)] ; don't append 'nil' nodes for already loaded 
      (recur parent remaining (conj nodes node) aloaded-set)))) ; loop 

; load dependencies, build dependency node
; TODO handle excluded-set
(defn dependency-tree-for-artifact [parent loaded-set excluded-set]
  (let [dependencies (get-dependencies parent)]
    (if-not (seq dependencies)
      [(new-dependency-node parent []) loaded-set] ; build and return dependency node for parent with no children
      (let [dep-as (map new-artifact dependencies)]
        (dependency-tree-children parent dep-as [] loaded-set excluded-set)))))

; TODO handle excluded-set
(defn dependency-tree [parent loaded-set excluded-set]
  "build the dependency tree"
  (if-not (loaded-artifact? loaded-set parent)
    (let [loaded (loaded-artifact loaded-set parent)]
      (dependency-tree-for-artifact parent loaded excluded-set))
    [(new-dependency-node parent []) loaded-set])) ; build and return dependency node for parent with no children

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
; create dot graph of the dependencies TODO use writer instead of println
;
(defn dependency-dot-vertex [artifact]
  "render a vertex for the artifact in the dot representation of the dependency tree"
  (str "\"" (artifact-name-version (:artifact artifact))"\""))

(defn dependency-dot-edge [parent child]
  "render an edge in the dot representation of the dependency tree"
  (if (:exclude (:artifact child))
    (str (dependency-dot-vertex parent) " -> [style=dashed] " (dependency-dot-vertex child) "\n")
    (str (dependency-dot-vertex parent) " -> " (dependency-dot-vertex child) "\n")))

(defn dependency-dot [node]
  "render a dot representation of the node"
  (doseq [include (includes node)]
    (print (dependency-dot-edge node include))
    (dependency-dot include))
  (doseq [exclude (excludes node)]
    (print (dependency-dot-edge node exclude))
    (dependency-dot exclude)))

(defn dependencies-dot [root-node]
  "render a dot representation of the dependency tree"
  (println "digraph Dependencies {
    outputmode=nodefirst;
    node [shape=plaintext fontsize=9];")
  (dependency-dot root-node)
  (println "}"))

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
  (let [root-artifact (new-artifact (param :project) (param :name) (param :version) "root")
        [dependency-root loaded-set] (dependency-tree root-artifact {})
        [q in ex] (process-dependency-tree (:dependencies dependency-root) {} #{} #{})]
    (log :trace "DEPS" dependency-root)
    (log :trace "Excluded:" ex)
    (log :trace "Included:" in)
    ;(process-artifacts in) ; process included artifacts
    ; TODO old way, remove when transitive dependencies work
    (process-dependencies (param :dependencies)) ; FIXME build a tree with only direct dependencies and use it for processing
    ;(if (param :deps-report)
    ;  (spit (param "${deps-report-dir}/dependencies.dot") (dependencies-dot dependency-root)))
    ))

(defn deps-distribute []
  (log :info "distributing artifacts...")
  (when (code-module?)
    (deps-distribute-jars))
  (when (web-module?)
    ) ; TODO distribute war files here or use publish?!?
  (when (data-module?)
    (distribute-artifact dev-repository-path
                         (new-artifact (param :project) (param :name) (param :version) "runtime" (param :name) "zip")))
  (when (plugin? "mdsd")
    (distribute-artifact dev-repository-path
                         (new-artifact (param :project) (param :name) (param :version) "model" (param :mdsd-model-name) "xmi") (param :mdsd-model-dir))))

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
                 [:distribute deps-distribute]])
  (log :debug "registered repositories" (param :deps-repositories)))
