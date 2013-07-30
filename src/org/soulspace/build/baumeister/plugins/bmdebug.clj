(ns org.soulspace.build.baumeister.plugins.bmdebug
  (:use [clojure.pprint]
        [org.soulspace.build.baumeister.config registry parameter-registry plugin-registry]
        [org.soulspace.build.baumeister.utils log]))

(defn bmdebug-init []
  (pprint (get-param-registry)))

(def bmdebug-config
  {:params []
   :functions [[:init bmdebug-init]]})

;
; plugin initialization
;
(defn plugin-init []
  (log :info "initializing plugin depsdot")
  (register-plugin "bmdebug")
  (register-vars (:params bmdebug-config))
  (register-fns (:functions bmdebug-config)))
