(ns org.soulspace.build.baumeister.dependency.dependency-node
  (:require [clojure.zip :as zip])
  (:use [clojure.set :only [union]]
        [org.soulspace.build.baumeister.utils log]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories artifact]
        [org.soulspace.build.baumeister.dependency dependency]
        ))

;
; Transitive dependency functions 
;
(def ^:dynamic loaded #{})
(def ^:dynamic built-nodes [])

(defn print-dependency
  ([dependency]
    (print-dependency dependency (:target dependency)))
  ([dependency target]
    (let [artifact (:artifact dependency)]
      (str "[" 
           (clojure.string/join  ", " 
                                 [(:project artifact) (:module artifact) (artifact-version artifact) (:name artifact) (:type artifact) target])
           "]"))))
  
(defn print-node [node]
  (print-dependency (:dependency node) (:target node)))

(defn matches-node? [dependency target node]
  (and (= (:dependency node) dependency) (= (:target node) target)))

(defn find-node [dependency target]
  (first (filter (partial matches-node? dependency target) built-nodes)))

(defn is-built? [dependency target]
  (let [node (find-node dependency target)]
    (not (nil? node))))

(defn is-excluded? [excluded dependency]
  (let [excluding-patterns (filter #(matches-artifact? % (:artifact dependency)) excluded)]
    (log :trace "is-excluded?" (:artifact dependency) "<<<" excluded ">>>" (seq excluding-patterns))
    (not (nil? (seq excluding-patterns)))))

(defn is-loaded? [dependency]
  (let [loaded-patterns (filter #(matches-artifact? % (:artifact dependency)) loaded)]
    (log :trace "is-loaded?" (:artifact dependency) "<<<" loaded ">>>" (seq loaded-patterns))
    (not (nil? (seq loaded-patterns)))))

(defn map-target [parent-target dependency-target]
  "Maps the target of a dependency according to the parents target."
  (((param :dependency-target-mapping) parent-target) dependency-target))

(defn get-transitive-dependency-data [dependency]
  "get the transitive dependencies configuration for the specified dependency"
  (if (= (:target dependency) :root)
    (param :dependencies) ; current module, use config
    (query-dependencies (param :deps-repositories) dependency)))

;
; Dependency node
;
(defprotocol DependencyNode
  ; TODO functions
  )

; A node in the transitive dependency tree
; contains a dependency, included nodes, excluded artifact patterns?
(defrecord DependencyNodeImpl
  [dependency target included excluded]
  DependencyNode
  ; TODO functions
  )

(defn new-dependency-node
  ([dependency target]
    (new-dependency-node dependency target [] #{}))
  ([dependency target included]
    (new-dependency-node dependency target included #{}))
  ([dependency target included excluded]
    (DependencyNodeImpl. dependency target included excluded)))

;
; dependency zipper tree editing
;
(defn depnode-branch? [node]
  true)

(defn depnode-children [node]
  (:included node))

(defn depnode-make [node children]
  (assoc node :included children)
  )

(defn depnode-zipper [node]
  (zip/zipper depnode-branch? depnode-children depnode-make node))


(defn find-or-build-node [dependency target included]
  (if-let [node (find-node dependency target)]
    (do
      ;(println "FOUND" (print-node node))
      node)
    (let [node (new-dependency-node dependency target included)]
      ;(println "BUILT" (print-node node))
      ;(println "NOT FOUND IN " (str (clojure.string/join ",\n" (map print-node built-nodes))))
      (def built-nodes (conj built-nodes node))
      node)))

(defn build-dependency-node 
  "Recursively build dependency nodes depth first, use built-nodes set as cache."  
  ([target excluded dependency]
    (build-dependency-node target excluded dependency false))
  ([target excluded dependency follow-optional]
    ; get transitive dependency data for the current dependency from its module descriptor and convert it to transitive dependencies
    (let [transitive-dep-data (get-transitive-dependency-data dependency)
          transitive-deps (map #(apply new-dependency %) transitive-dep-data)
          included
          (loop [deps transitive-deps inclusions []]
            ; build a list of dependency nodes as inclusions, for all children that are not already loaded or excluded
            (if (seq deps)
              (let [dep (first deps)
                    node-target (map-target target (:target dep))] ; compute the nodes target, which depends on the parent target (e.g. :aspect -> :runtime)
                ; excluded or optional dependencies are are not added to the included list of this dependency
                (if (or (is-excluded? excluded dep) ; dependency is excluded
                        (and (:optional dep) (not follow-optional)) ; don't include transitive optional dependencies
                        (nil? node-target)) ; dependency has not to be included (e.g. transitive :aspectin)
                  (recur (rest deps) inclusions)
                  (recur (rest deps) (conj inclusions (build-dependency-node node-target (union excluded (:exclusions dep)) dep)))))
              inclusions))]
      (find-or-build-node dependency target included))))
  
(defn root-dependency []
  (new-dependency [(param :project) (param :module) (param :version)] :root))

(defn set-dependency-target [dependency target]
  (assoc dependency :target target))

(defn process-tree [queue dependencies]
  "Generate a sequence of dependencies by processing the dependency tree in breadth first order."
  (if (seq queue)
    (let [node (first queue)
          node-dependency (set-dependency-target (:dependency node) (:target node))]
      (if (or
            (= (:target node) :root) ; don't include root, it's the module itself
            (seq (filter #(compatible-dependency? node-dependency %) dependencies))) ; already included
        (recur (concat (rest queue) (:included node)) dependencies) ; concat included children anyway, they can have new transitive dependencies
        (recur (concat (rest queue) (:included node)) (conj dependencies node-dependency)))) ; not yet included, include dependency and add children
    dependencies) ; return the processed dependencies
  )

(defn build-dependency-tree []
  (log :debug "doing build-dependency-tree")
  (def loaded #{}) ; reset loaded set
  (def built-nodes []) ; reset loaded set
  (let [tree (build-dependency-node :root (into #{} (map new-artifact-pattern (param :dependency-excludes))) (root-dependency))]
    ;(println "DEPENDENCIES")
    ;(println (str (clojure.string/join ",\n" (map print-dependency (process-tree [tree] [])))))
    tree))

