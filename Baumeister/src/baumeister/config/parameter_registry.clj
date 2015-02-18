;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.config.parameter-registry
  (:use [clojure.pprint]
        [org.soulspace.clj.application string-property]))

;
; parameter registry
;
(def ^{:dynamic true :private true} param-registry)

(defn get-param-registry []
  "Returns the parameter registry."
  param-registry)

(defn reset-param-registry []
  "Resets the parameter registry."
  (def param-registry {}))

(defn print-parameters []
  (pprint (get-param-registry)))

; replace "${build-dir}/report" with (str (get-var (keyword build-dir) "${build-dir}") "/dir")
(defn replace-vars
  [value]
    (replace-properties param-registry value))

(defn register-param-as-is [key value]
  "Register the key/value pair without any preprocessing"
  (def param-registry (assoc param-registry key value)))

; TODO refactor to multimethod?
(defn register-param [key value]
  "Register the key/value pair with preprocessing (e.g. variable replacement)"
  (def param-registry
    (cond
      (string? value)
      (assoc param-registry key (replace-vars value))
      (vector? value)
      (assoc param-registry key (map replace-vars value))
      (set? value)
      (assoc param-registry key value)
      (map? value)
      (assoc param-registry key value)
      (seq? value)
      (assoc param-registry key (map replace-vars value))
      :default
      (do 
        (assoc param-registry key value)))))

; TODO support documentation on vars
(defn register-params [vars]
  "Register parameters in the parameter registry."
  (doseq [[key value] vars]
    (register-param key value)))
