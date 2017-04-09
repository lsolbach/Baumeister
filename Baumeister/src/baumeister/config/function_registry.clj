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
(def fn-registry (atom {})) ; registry for plugin step functions
(def step-fn-registry (atom {})) ; registry for plugin functions

(defn reset-step-fn-registry
  "Resets the step function registry."
  []
  (reset! step-fn-registry {}))

(defn reset-fn-registry
  "Resets the function registry."
  []
  (reset! fn-registry {}))

(defn reset-fn-registries
  "Resets the function registries."
  []
  (reset-fn-registry)
  (reset-step-fn-registry))

(defn register-step
  "Registers a function at the step."
  [step function]
  (let [step-key (keyword step)]
    (swap! step-fn-registry assoc step-key (conj (get step-fn-registry step-key []) function))))

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
  (let [step-key (keyword step)]
    (swap! fn-registry assoc step-key (conj (get fn-registry step-key []) function))))

(defn register-functions
  "Registers the functions at their names."
  [functions]
  (doseq [[name function] functions]
    (register-function name function)))

(defn get-registered-functions
  "Returns the functions for the step."
  [step]
  (fn-registry step))
