(ns baumeister.plugin.graphviz
  (:use [clojure.java.shell]
        [org.soulspace.clj file file-search]
        [baumeister.utils log]
        [baumeister.config registry]))

; TODO add execute neato, ...

(defn outfile
  "Build an output filename from the input file name and the file type."
  [type dot-file]
  (str (base-name dot-file) "." type))

(defn find-dot-files
  "Find dot files."
  []
  (existing-files-on-path "dot" "."))

(defn generate-dot-graphics
  "Generates an image of type from dot file."
  [type in out]
  (log :trace (param "${graphviz-bin-dir}/dot") (str "-T" type) (str "-O " (str in)))
  (log :trace :out (sh (param "${graphviz-bin-dir}/dot") (str "-T" type) (str "-O " (str in)))))

(defn graphviz-generate-images
  []
  (doseq [dot-file (find-dot-files)]
    (generate-dot-graphics "png" dot-file (outfile "png" dot-file))))

(defn graphviz-post-generate
  []
  (graphviz-generate-images))

(def config
  {:param [[:graphviz-bin-dir "/usr/bin/"]
           [:graphviz-dot-format "png"]]
   :steps [[:post-generate graphviz-post-generate]]
   :functions []})
