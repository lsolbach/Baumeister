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
(def ^:dynamic built-nodes #{})

(defn matches-node? [dependency target node]
  (and (=(:dependency node) dependency) (= (:target node) target)))

(defn find-node [dependency target]
;  (println "Searching node for" dependency ":" target "in" built-nodes)
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
; dependency tree functions
;
(defn build-dependency-node [target excluded dependency]
  "Recursively build dependency nodes depth first."
   ; add the current dependency to the loaded set
  (def loaded (conj loaded (new-artifact-pattern (:artifact dependency))))
   ; get transitive dependency data for the current dependency from its module descriptor
   ; and convert it to transitive dependencies
  (let [transitive-dep-data (get-transitive-dependency-data dependency)
        transitive-deps (map #(apply new-dependency %) transitive-dep-data)
        included
        (loop [deps transitive-deps
               inclusions []]
          ; build a list of dependency nodes as inclusions, for all children that are not already loaded or excluded
          (if (seq deps)
            (let [dep (first deps)
                  exclusions (union excluded (:exclusions dep))
                  node-target (map-target target (:target dep))]
              ; already loaded dep-nodes are not added to the included list of this dependency
              (if (or (is-excluded? excluded dep)
                      ;(is-loaded? dep)
                      (nil? node-target) (:optional dep))
                (recur (rest deps) inclusions)
                (recur (rest deps)
                       (conj inclusions (build-dependency-node
                                          node-target exclusions dep)))))
            inclusions))]
    (new-dependency-node dependency target included excluded)))

(defn build-dependency-node2 [target excluded dependency]
  "Recursively build dependency nodes depth first."
  ; add the current dependency to the loaded set
  (def loaded (conj loaded (new-artifact-pattern (:artifact dependency))))
  ; get transitive dependency data for the current dependency from its module descriptor
  ; and convert it to transitive dependencies
  (let [transitive-dep-data (get-transitive-dependency-data dependency)
        transitive-deps (map #(apply new-dependency %) transitive-dep-data)
        included
        (loop [deps transitive-deps
               inclusions []]
          ; build a list of dependency nodes as inclusions, for all children that are not already loaded or excluded
          (if (seq deps)
            (let [dep (first deps)
                  exclusions (union excluded (:exclusions dep))
                  ;node-target (map-target target (:target dep))
                  node-target (:target dep)
                  node (find-node dep node-target)
                  ]
              ; already loaded dep-nodes are not added to the included list of this dependency
              (if (or (is-excluded? excluded dep)
                      ;(is-loaded? dep)
                      ;(nil? (map-target target (:target dep)))
                      (:optional dep)
                      )
                (recur (rest deps) inclusions)
                (if (not (nil? node))
                  (recur (rest deps) (conj inclusions node))
                  (recur (rest deps)
                         (conj inclusions (build-dependency-node2
                                          node-target exclusions dep))))))
            inclusions))]
    (let [node (new-dependency-node dependency target included excluded)]
      (def built-nodes (conj built-nodes node))
      node
      )))


(defn root-dependency []
  (new-dependency [(param :project) (param :module) (param :version)] :root))

(defn build-dependency-tree []
  (log :debug "doing build-dependency-tree")
  (def loaded #{}) ; reset loaded set
  (build-dependency-node2 :root #{} (root-dependency)))


;
; zipper tree editing
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

(defn depnode-build-tree [zipper dependency]
  
  )
