(ns org.soulspace.build.baumeister.utils.property)

; concatenate the tokens matched by the pattern of replace-properties
; if no property is found, replace with
(defn- concat-property-tokens [prop-map [_ t1 t2 t3]]
  (str t1 (get prop-map (keyword t2) (str "${" t2 "}")) t3))

; replace "${build-dir}/report" with (str (get-var (keyword build-dir) "${build-dir}") "/dir") (TODO: recursivly?)
(defn replace-properties
  "Replaces properties of the form ${property} in strings contained in input."
  ([prop-map input]
    (cond
      (string? input)
      (if-let [tokens (re-seq #"([^$]*)(?:\$\{([^}]*)\}*([^$]*))" input)]
        (do
          (reduce str (map (partial concat-property-tokens prop-map) tokens)))
        input)
      (coll? input)
      (map (partial replace-properties prop-map) input)
      :default
      input))
  ([prop-map input default]
    (if-not (nil? input)
      (replace-properties prop-map input)
      (replace-properties prop-map default))))
