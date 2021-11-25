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
  (:require [clojure.pprint :as pp]
            [org.soulspace.clj.property-replacement :as props]))

(def param-registry (atom {})) ; parameter registry

(defn get-param-registry
  "Returns the parameter registry."
  []
  @param-registry)

(defn reset-param-registry
  "Resets the parameter registry."
  []
  (reset! param-registry {}))

(defn print-parameters
  "Prints the parameter registry."
  []
  (pp/pprint (get-param-registry)))

(defn replace-vars
  "Replaces with the variables in the parameter registry with the given value."
  [value]
  ; replace "${build-dir}/report" with (str (get-var (keyword build-dir) "${build-dir}") "/dir")
  (props/replace-properties param-registry value))

(defn register-param-as-is
  "Registers the key/value pair without any preprocessing."
  [key value]
  (swap! param-registry assoc key value))

; TODO refactor to multimethod?
(defn assoc-param
  "Associate a parameter."
  [m key value]
  (println "assoc" key value)
  (cond
    (string? value)
    (assoc m key (replace-vars value))
    (keyword? value)
    (assoc m key value)
    (vector? value)
    (assoc m key (map replace-vars value))
    (set? value)
    (assoc m key value)
    (map? value)
    (assoc m key value)
    (seq? value)
    (assoc m key (map replace-vars value))
    :default
    (assoc m key value)))

(defn register-param
  "Registers the key/value pair with preprocessing (e.g. variable replacement)"
  [key value]
  (swap! param-registry assoc-param key value))

; TODO support documentation on vars
(defn register-params
  "Registers parameters in the parameter registry."
  [vars]
  (doseq [[key value] vars]
    (register-param key value)))
