;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.dependency.dependency-dot
  (:use [org.soulspace.clj.artifact artifact]
        [baumeister.dependency dependency]))

;
; create dot graph of the dependencies
;

(def ^:dynamic processed-edges #{})

(def edge-color
 {:runtime   "black"
  :dev       "green"
  :aspect    "red"
  :aspectin  "orange"
  :model     "blue"
  :generator "cyan"})

(defn dot-edge-style
  "Returns the edge style for the edge between parent and child."
  [parent child]
  (str "[color=" (edge-color (:target (:dependency child)))
       " style=" (if (:optional (:dependency child)) "dotted" "solid") "]"))

(defn dependency-dot-vertex
  "Returns a vertex for the artifact in the dot representation of the dependency tree."
  [dep-node]
  (let [artifact (:artifact (:dependency dep-node))]
    (str "\"" (artifact-key artifact) "\""
         " [shape=box label=\"" (:project artifact) "\\n" (artifact-name-version artifact) "\"];")))

(defn dependency-dot-edge-end
  "Returns a edge end for the artifact in the dot representation of the dependency tree."
  [dep-node]
  (let [artifact (:artifact (:dependency dep-node))]
    (str "\"" (artifact-key artifact) "\"")))

(defn dependency-dot-edge
  "Returns an edge in the dot representation of the dependency tree."
  [parent child]
  (let [dot-edge (str (dependency-dot-edge-end parent) " -> " (dependency-dot-edge-end child)
                      " " (dot-edge-style parent child) ";")]
    (if-not (contains? processed-edges dot-edge)
      (do (def processed-edges (conj processed-edges dot-edge))
        dot-edge)
      "")))

(defn dependency-vertices
  "Renders a dot representation of the node."
  [node]
  (println (dependency-dot-vertex node))
  (if (seq (:included node))
    (doseq [include (:included node)]
      (dependency-vertices include))))

(defn dependency-edges
  "Recursively renders the edges of the dependency graph."
  [node]
  (if (seq (:included node))
    (doseq [include (:included node)]
      (println (dependency-dot-edge node include))
      (dependency-edges include))))

(defn dependencies-dot
  "Renders a dot representation of the dependency tree"
  ([writer root-node]
    (def processed-edges #{})
    (binding [*out* writer]
      (println "digraph Dependencies {
    outputmode=nodefirst;
    node [shape=plaintext fontsize=9];")
      (dependency-vertices root-node)
      (dependency-edges root-node)
      (println "}"))))
