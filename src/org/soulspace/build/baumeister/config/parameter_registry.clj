(ns org.soulspace.build.baumeister.config.parameter-registry)

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

(declare replace-vars)

(defn register-param-as-is [key value]
  "Register the key/value pair without any preprocessing"
  (def param-registry (assoc param-registry key value)))

; TODO refactor to multimethod
; TODO merge seqs/vectors if a seq/vector var is already registered?!?
; TODO  to e.g. handle default repositories in module_defaults.clj and additional module specific repositories
; TODO check if this is a desired behaviour for all seq/vector vars (always add, never override?)
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

; concatenate the tokens matched by the pattern of replace vars
(defn concat-tokens [vars [_ t1 t2 t3]]
  (str t1 (get vars (keyword t2) (str "${" t2 "}")) t3))

; replace "${build-dir}/report" with (str (get-var (keyword build-dir) "${build-dir}") "/dir") (TODO: recursivly?)
(defn replace-vars
  ([vars value]
    (cond
      (string? value)
      (if-let [tokens (re-seq #"([^$]*)(?:\$\{([^}]*)\}*([^$]*))" value)]
        (reduce str (map (partial concat-tokens vars) tokens))
        value)
      (coll? value)
      (map (partial replace-vars vars ) value)
      :default
      value))
  ([value]
    (replace-vars param-registry value)))

