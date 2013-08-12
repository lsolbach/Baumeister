(ns baumeister.plugin.markdown
  (:use [clojure.java.io :only [as-file reader]]
        [org.soulspace.clj file]
        [org.soulspace.clj.markdown markdown]
        [baumeister.config registry]))

(defn readme
  []
  (spit (str (param :markdown-build-dir) "/README.md")
        (str
          (h1 (param "${module}"))
          (p (param "${description}"))
          (h2 "Version")
          (p (param :version))
          (h2 "Author")
          (p (param :author))
          (h2 "Copyright")
          (p (param :copyright))
          (h2 "License")
          (str (link (param :license)) "\n")
          (h2 "Documentation")
          (h2 "Code Repository"))))

(defn version-history
  []
  )

(defn markdown-init
  []
  (create-dir (as-file (str (param :markdown-build-dir)))))


(defn markdown-generate-distribution
  []
  (readme))

(def config
  {:params [[:markdown-build-dir "${build-dir}/markdown"]]
   :functions [[:init markdown-init]
               [:generate-distribution markdown-generate-distribution]]})
