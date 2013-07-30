(ns org.soulspace.build.baumeister.utils.property)

; concatenate the tokens matched by the pattern of replace vars
(defn concat-property-tokens [vars [_ t1 t2 t3]]
  (str t1 (get vars (keyword t2) (str "${" t2 "}")) t3))

; replace "${build-dir}/report" with (str (get-var (keyword build-dir) "${build-dir}") "/dir") (TODO: recursivly?)
(defn replace-properties
  "Replaces properties of the form ${property} in strings contained in value."
  ([prop-map value]
    (cond
      (string? value)
      (if-let [tokens (re-seq #"([^$]*)(?:\$\{([^}]*)\}*([^$]*))" value)]
        (do
          (reduce str (map (partial concat-property-tokens prop-map) tokens)))
        value)
      (coll? value)
      (map (partial replace-properties prop-map) value)
      :default
      value))
  ([prop-map value default]
    (if-not (nil? value)
      (replace-properties prop-map value)
      (replace-properties prop-map default)
      )))
