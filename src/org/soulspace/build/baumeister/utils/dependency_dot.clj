(ns org.soulspace.build.baumeister.utils.dependency-dot
  (:use [org.soulspace.build.baumeister.utils artifact dependency]))

;
; create dot graph of the dependencies TODO use writer instead of println (re-bind *out*?!)
;
(defn dependency-dot-vertex [artifact]
  "render a vertex for the artifact in the dot representation of the dependency tree"
  (str "[label=<" (artifact-name-version (:artifact artifact))">]"))

(defn dependency-dot-edge [parent child]
  "render an edge in the dot representation of the dependency tree"
  (if (:exclude (:artifact child))
    (str (dependency-dot-vertex parent) " -> [style=dashed color=red] " (dependency-dot-vertex child) "\n")
    (str (dependency-dot-vertex parent) " -> " (dependency-dot-vertex child) "\n")))

(defn dependency-dot [node]
  "render a dot representation of the node"
  (doseq [include (includes node)]
    (print (dependency-dot-edge node include))
    (dependency-dot include))
  (doseq [exclude (excludes node)]
    (print (dependency-dot-edge node exclude))
    (dependency-dot exclude)))

(defn dependencies-dot [writer root-node]
  "render a dot representation of the dependency tree"
  (binding [*out* writer]
    (println "digraph Dependencies {
    outputmode=nodefirst;
    node [shape=plaintext fontsize=9];")
    (dependency-dot root-node)
    (println "}")))

