(ns org.soulspace.build.baumeister.dependency.dependency-tree
  (:use [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.dependency dependency]
        [org.soulspace.build.baumeister.utils log]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories]))

(defprotocol DependencyNode
  ; TODO functions
  )

; A node in the transitive dependency tree
; contains a dependency, included nodes, excluded artifact patterns?
(defrecord DependencyNodeImpl
  [dependency included excluded]
  ; TODO functions
  )

(defn new-dependency-node
  ([dependency]
    (new-dependency dependency [] {}))
  ([dependency included]
    (new-dependency dependency included {}))
  ([dependency included excluded]
    (DependencyNodeImpl. dependency included excluded)))

(defn build-dependency-node [dependency loaded excluded]
  ""
; build list of dependency nodes as included, for all children that are not already loaded or excluded
  
  (new-dependency-node dependency [] ; <- included
                       ))








;
; transitive dependencies management
;
(declare build-dependency-tree)

(defn loaded-artifact [loaded artifact]
  "marks an artifact as loaded"
  (log :debug "marking artifact loaded" (artifact-key artifact))
  (conj loaded (artifact-key artifact)))

(defn loaded-artifact? [loaded artifact]
  "tests if an artifact is already loaded"
  (contains? loaded (artifact-key artifact)))

(defn get-transitive-dependencies [dependency]
  "get the dependencies configuration for the specified dependency"
  (if (= (:target dependency) "root")
    (param :dependencies) ; current module, use config
    (query-dependencies (param :deps-repositories) dependency)))

(defn build-dependency [dep]
  (println "====> build-dependency: " dep)
  (new-dependency dep))

; consume artifacts, build nodes
; TODO handle excluded-set
(defn build-dependency-tree-children [parent artifacts nodes loaded-set excluded-set]
  (println "build-dependency-tree-children:" parent artifacts nodes loaded-set excluded-set)
  (if-not (seq artifacts)
    (do
      (println ">>>>" parent nodes loaded-set)
      [(new-dependency parent nodes) loaded-set] ; build and return dependency node for parent with the subtrees as children
      )
    (let [actual (first artifacts)
          remaining (rest artifacts)
          [node aloaded-set] (build-dependency-tree actual loaded-set excluded-set) ; build subtree for actual artifact (recursive bottom up because of persistent data structures)
          anodes (if (seq node) (conj nodes node) nodes)] ; don't append 'nil' nodes for already loaded 
      (recur parent remaining (conj nodes node) aloaded-set excluded-set)))) ; loop 

; load dependencies, build dependency node
; TODO handle excluded-set
(defn build-dependency-tree-for-artifact [parent loaded-set excluded-set]
  (println "build-dependency-tree-for-artifact:" parent loaded-set excluded-set)
  (let [dependencies (get-transitive-dependencies parent)]
    (if-not (seq dependencies)
      [(new-dependency parent []) loaded-set] ; build and return dependency node for parent with no children
      ; FIXME dependencies is actually now a vector of an artifact and an exclusion seq
      (do
        (println "build-dependency-tree-for-artifact2:" dependencies)
        (let [dep-as (map build-dependency dependencies)] ; FIXME handle vector instead of mapping first
          (println "==> build-dependency-tree-for-artifact3:" dep-as)
          (build-dependency-tree-children parent dep-as [] loaded-set excluded-set))))))

; TODO handle excluded-set
(defn build-dependency-tree [parent loaded-set excluded-set]
  "build the dependency tree"
  (println "build-dependency-tree:" parent loaded-set excluded-set)
  (if-not (loaded-artifact? loaded-set parent)
    (let [loaded (loaded-artifact loaded-set parent)]
      (build-dependency-tree-for-artifact parent loaded excluded-set))
    [(new-dependency parent []) loaded-set])) ; build and return dependency node for parent with no children


; For a dependency vector, a set of visited dependencies and a set of excluded dependencies,
; build a list of sub dependency tree nodes and build and return a dependency tree node
; for the dependency vector containing the artifact, type, scope and a list of the included
; sub dependency tree nodes and the excluded dependencies (artifact patterns?)

;(defn build-dependency-tree [dependency-vector visited excluded])

;(defn build-tree-for-dependencies [deps included excluded])
