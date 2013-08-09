(ns baumeister.dependency.dependency-plugins
  (:use [org.soulspace.clj.application classpath]
        [baumeister.config registry]
        [baumeister.dependency dependency dependency-node dependency-initialization dependency-dot]))


(defn build-plugin-dependency-tree []
  (log :debug "doing build-plugin-dependency-tree")
  (def loaded #{}) ; reset loaded set
  (def built-nodes []) ; reset loaded set
  (let [tree (build-dependency-node :plugin-root [] (into #{} (map new-artifact-pattern (param :dependency-excludes))) (root-dependency))]
    tree))


(defn plugin-dependency-classpath []
  ""
  (let [plugin-cp (artifact-urls (process-tree [(build-plugin-dependency-tree)] []))]
    (register-classpath-urls plugin-cp)
    (println (urls))))

