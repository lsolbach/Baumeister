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
        [baumeister.dependency dependency dependency-transitivity]))

(def ^:dynamic processed-edges #{})

;
; create dot graph of the dependencies TODO use writer instead of println (re-bind *out*?!)
;
(def edge-style
  {:runtime   "[color=black]"
   :dev       "[style=dashed color=black]"
   :aspect    "[color=black]"
   :aspectin  "[color=brown]"
   :model     "[color=blue]"
   :generator "[color=cyan]"
   })

(defn dependency-dot-vertex [dep-node]
  "render a vertex for the artifact in the dot representation of the dependency tree"
  (let [artifact (:artifact (:dependency dep-node))]
    (str "\"" (artifact-key artifact) "\""
         " [label=\"" (:project artifact) "\\n" (artifact-name-version artifact) "\"];")))

(defn dependency-dot-edge-end [dep-node]
  "render a edge end for the artifact in the dot representation of the dependency tree"
  (let [artifact (:artifact (:dependency dep-node))]
    (str "\"" (artifact-key artifact) "\"")))

(defn dependency-dot-edge [parent child]
  "render an edge in the dot representation of the dependency tree"
  (let [dot-edge (str (dependency-dot-edge-end parent) " -> " (dependency-dot-edge-end child)
                      " " (edge-style (:target (:dependency child))) ";")]
    (if-not (contains? processed-edges dot-edge)
      (do (def processed-edges (conj processed-edges dot-edge))
        dot-edge)
      "")))

(defn dependency-vertices [node]
  "render a dot representation of the node"
  (println (dependency-dot-vertex node))
  (if (seq (:included node))
    (doseq [include (:included node)]
      (dependency-vertices include))))

(defn dependency-edges [node]
  "recursively render the edges of the dependency graph"
  (if (seq (:included node))
    (doseq [include (:included node)]
      (println (dependency-dot-edge node include))
      (dependency-edges include))))

(defn dependencies-dot [writer root-node]
  "render a dot representation of the dependency tree"
  (def processed-edges #{})
  (binding [*out* writer]
    (println "digraph Dependencies {
    outputmode=nodefirst;
    node [shape=plaintext fontsize=9];")
    (dependency-vertices root-node)
    (dependency-edges root-node)
    (println "}")))
