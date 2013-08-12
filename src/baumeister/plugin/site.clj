(ns baumeister.plugin.site
  (:use [org.soulspace.clj.xhtml.xhtml-dsl]))


(defn generate-overview
  []
  )

(defn generate-documentation
  []
  )

(defn generate-reports
  []
  )

(defn site-generate
  []
  (println "Site")
  )


(def config 
  {:params [:site-css ""
            ]
   :functions []})
