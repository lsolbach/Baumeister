;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.config.function-registry)

;
; function registry
;
(def ^{:dynamic true :private true} fn-registry) ; registry for plugin step functions
(def ^{:dynamic true :private true} step-fn-registry) ; registry for plugin functions

(defn reset-step-fn-registry
  "Resets the step function registry."
  []
  (def step-fn-registry {}))

(defn reset-fn-registry
  "Resets the function registry."
  []
  (def fn-registry {}))

(defn reset-fn-registries
  "Resets the function registries."
  []
  (reset-fn-registry)
  (reset-step-fn-registry))

(defn register-step
  "Registers a function at the step."
  [step function]
  (let [stp (keyword step)]
    (def step-fn-registry
      (assoc step-fn-registry stp (conj (get step-fn-registry stp []) function)))))

(defn register-steps
  "Registers the functions at their steps."
  [functions]
  (doseq [[step function] functions]
    (register-step step function)))

(defn get-registered-step-functions
  "Returns the functions for the step."
  [step]
  (step-fn-registry step))

(defn register-function
  "Registers a function at the step."
  [step function]
  (let [stp (keyword step)]
    (def fn-registry
      (assoc fn-registry stp (conj (get fn-registry stp []) function)))))

(defn register-functions
  "Registers the functions at their names."
  [functions]
  (doseq [[name function] functions]
    (register-function name function)))

(defn get-registered-functions
  "Returns the functions for the step."
  [step]
  (fn-registry step))
