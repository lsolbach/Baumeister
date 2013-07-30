(ns org.soulspace.build.baumeister.config.function-registry)

;
; function registry
;
(def ^{:dynamic true :private true} fn-registry) ; registry for plugin functions

(defn reset-fn-registry [] (def fn-registry {}))
(defn register-function [build-step function]
  (let [step (keyword build-step)]
    (def fn-registry
      (assoc fn-registry step (conj (get fn-registry step []) function)))))

(defn register-functions [functions]
  (doseq [[build-step function] functions]
    (register-function build-step function)))

(defn get-registered-functions [step]
  (fn-registry step))