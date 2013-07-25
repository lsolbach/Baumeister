(ns org.soulspace.build.baumeister.dependency.dependency-dot
  (:use [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.dependency dependency dependency-node]))

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
  (str (dependency-dot-edge-end parent) " -> " (dependency-dot-edge-end child)
       " " (edge-style (:target (:dependency child))) ";"))

(defn dependency-dot-edge-excluded [parent child]
  "render an excluded edge in the dot representation of the dependency tree"
  (str (dependency-dot-edge-end parent) " -> " (dependency-dot-edge-end child)
       " [style=dotted color=red];"))

(defn dependency-vertices [node]
  "render a dot representation of the node"
  (println (dependency-dot-vertex node))
  (if (seq (:included node))
    (doseq [include (:included node)]
      (dependency-vertices include)))
;  (if (seq (:excluded node))
;    (doseq [exclude (:excluded node)]
;      (dependency-excluded-vertices exclude)))
  )

(defn dependency-edges [node]
  "recursively render the edges of the dependency graph"
  (if (seq (:included node))
    (doseq [include (:included node)]
      (println (dependency-dot-edge node include))
      (dependency-edges include)))
;  (if (seq (excluded node))
;    (doseq [exclude (excluded node)]
;      (print (dependency-dot-edge node exclude))
;      (dependency-dot exclude)))
  )

(defn dependencies-dot [writer root-node]
  "render a dot representation of the dependency tree"
  (binding [*out* writer]
    (println "digraph Dependencies {
    outputmode=nodefirst;
    node [shape=plaintext fontsize=9];")
    (dependency-vertices root-node)
    (dependency-edges root-node)
    (println "}")))
