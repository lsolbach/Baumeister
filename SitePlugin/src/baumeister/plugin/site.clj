(ns baumeister.plugin.site
  )

(defn site-structure
  "Returns the structure of the site to generate."
  []
  ; compute the structure of the site to be generated
  )

(defn generate-page
  "Generate a page of the site."
  [structure page]
  )

(defn site-clean
  []
  )

(defn site-init
  []
  )

(defn site-generate
  "Site generation."
  []
  ; get the site structure
  (let [site-struct (site-structure)]
    ; iterate over site structure and generate pages
    ))

(def config 
  {:params [:site-css ""]
   :steps []
   :functions []})
