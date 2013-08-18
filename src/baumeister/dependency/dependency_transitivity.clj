;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.dependency.dependency-transitivity
  (:require [clojure.zip :as zip])
  (:use [clojure.set :only [union]]
        [org.soulspace.clj.artifact artifact]
        [baumeister.utils log]
        [baumeister.config registry]
        [baumeister.repository repositories]
        [baumeister.dependency dependency]))

(defn maven-scope-to-target [maven-scope]
  "Maps a maven scope to a target"
  ((param :maven-scope-to-target) maven-scope))

(defn maven-type-to-type [maven-type]
  "Maps the maven type to a type."
  ((param :maven-type-to-type) maven-type))

;
; Transitive dependency functions 
;
(def ^:dynamic built-nodes [])

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

(defn cycle?
  [path dependency]
  (seq (filter #(= dependency %) path)))

(defn map-target [parent-target dependency-target]
  "Maps the target of a dependency according to the parents target."
  (((param :dependency-target-mapping) parent-target) dependency-target))

(defn get-transitive-dependency-data [dependency]
  "get the transitive dependencies configuration for the specified dependency"
  (cond
    (= (:target dependency) :root) (param :dependencies) ; current module, use config
    (= (:target dependency) :plugin-root) (filter coll? (param :plugins)) ; current module plugins 
    :default (query-dependencies dependency)))

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

(defn find-or-build-node [dependency target included]
  (if-let [node (find-node dependency target)]
    node
    (let [node (new-dependency-node dependency target included)]
      (def built-nodes (conj built-nodes node))
      node)))


(defn build-dependency-node
  "Recursively build dependency nodes depth first, use built-nodes set as cache."  
  ([target path excluded dependency]
    (build-dependency-node target path excluded dependency false))
  ([target path excluded dependency follow-optional]
    ; get transitive dependency data for the current dependency from its module descriptor and convert it to transitive dependencies
    (let [transitive-dep-data (get-transitive-dependency-data dependency)
          ; TODO handle POMDependencies differently
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
                        (nil? node-target) ; dependency has not to be included (e.g. transitive :aspectin)
                        (cycle? path dep)) ; dependency was seen on the way down, cycle!
                  (recur (rest deps) inclusions)
                  (recur (rest deps) (conj inclusions (build-dependency-node node-target (conj path dependency) (union excluded (:exclusions dep)) dep)))))
              inclusions))]
      (find-or-build-node dependency target included))))

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
    dependencies)) ; return the processed dependencies

(defn root-dependency 
  ([]
    (new-dependency [(param :project) (param :module) (param :version)] :root))
  ([target]
    (new-dependency [(param :project) (param :module) (param :version)] target)))

(defn build-dependency-tree []
  (log :debug "doing build-dependency-tree")
  (def built-nodes []) ; reset built nodes set
  (let [tree (build-dependency-node :root [] (into #{} (map new-artifact-pattern (param :dependency-excludes))) (root-dependency))]
    ;(println "DEPENDENCIES")
    ;(println (str (clojure.string/join ",\n" (map print-dependency (process-tree [tree] [])))))
    tree))

(defn build-plugin-dependency-tree []
  (log :debug "doing build-plugin-dependency-tree")
  (def built-nodes []) ; reset built nodes set
  (let [tree (build-dependency-node :plugin-root [] (into #{} (map new-artifact-pattern (param :dependency-excludes))) (root-dependency :plugin-root))]
    tree))

(defn get-dependencies []
  (if (or (param :dependeny-transitive) (= true (param :dependeny-transitive)))
    (let [root (build-dependency-tree)]
      (register-val :dependencies-tree root)
      (process-tree [root] []))
    (map #(apply new-dependency %) (param :dependencies))))
