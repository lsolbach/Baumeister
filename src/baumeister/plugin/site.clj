(ns baumeister.plugin.site
  (:use [org.soulspace.clj.xhtml.xhtml-dsl]))




(defn site-generate []
  (println "Site")
  )


(def config 
  {:params [:site-css ""
            ]
   :functions []})
