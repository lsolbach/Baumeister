(ns baumeister.dependency.dependency-plugins
  (:use [org.soulspace.clj.application classpath]
        [baumeister.config registry]
        [baumeister.utils log]
        [baumeister.dependency dependency dependency-node dependency-initialization dependency-dot]))



(defn plugin-dependency-classpath []
  ""
  (let [plugin-cp (artifact-urls (process-tree [(build-plugin-dependency-tree)] []))]
    (register-classpath-urls plugin-cp)
    (println (urls))))

