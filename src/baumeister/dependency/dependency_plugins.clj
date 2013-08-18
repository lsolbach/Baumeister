(ns baumeister.dependency.dependency-plugins
  (:use [org.soulspace.clj.application classpath]
        [baumeister.config registry]
        [baumeister.utils log]
        [baumeister.dependency dependency dependency-transitivity dependency-initialization dependency-dot]))

(defn plugin-dependency-classpath []
  ""
  (let [plugin-cp (artifact-urls (process-tree [(build-plugin-dependency-tree)] []))]
    (println "PLUGIN-CP" plugin-cp)
    (add-urls (rest plugin-cp)) ; drop plugin root, it's the current module
    (println "CP URLS")
    (doseq [url (urls)]
      (println url))))

