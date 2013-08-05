;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
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