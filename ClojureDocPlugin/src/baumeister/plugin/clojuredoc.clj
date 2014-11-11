(ns baumeister.plugin.clojuredoc
  ;(:use [])
  )

; TODO implement autodoc

(defn clojuredoc-sourcedoc
  []
  )

(def config
  {:params [[:clojuredoc-dir "${build-sourcedoc-dir}/clojure"]]
   :steps [[:sourcedoc clojuredoc-sourcedoc]]
   :functions []})
