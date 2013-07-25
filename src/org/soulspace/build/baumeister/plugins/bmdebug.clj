(ns org.soulspace.build.baumeister.plugins.bmdebug
  (:use [clojure.pprint]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils log]))

(defn bmdebug-init []
  (pprint var-registry))

;
; plugin initialization
;
(defn plugin-init []
  (log :info "initializing plugin depsdot")
  (register-plugin "bmdebug")
  (register-vars [])
  (register-fns [[:init bmdebug-init]]))
