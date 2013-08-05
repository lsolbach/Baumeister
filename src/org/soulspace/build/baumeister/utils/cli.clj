;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.utils.cli
  (:require [clojure.string :as str])
  (:use [org.soulspace.clj string file]))

; TODO move to application framework?
(def spec-entry-format
  {:name "define"
   :option "--define"
   :short "-D"
   :doc "Define a var"
   :parse-fn identity
   :flag false
   :default nil
   })

(defn flag-spec? [arg]
  (starts-with "--[no-]" arg))

(defn long-option? [arg]
  "Tests if the string is a long option, which starts with the string '--'."
  (starts-with "--" arg))

(defn option? [arg]
  "Tests if the string is an option, which starts with the character '-'."
  (starts-with "-" arg))

(defn matches-option? [arg spec]
  "Returns true, if the arg starts with an option switch of this spec"
  (if (long-option? arg)
    (and (not (nil? (:long spec))) (starts-with (:long spec) arg))
    (and (not (nil? (:short spec))) (starts-with (:short spec) arg))))

(defn option-name [opt]
  (str/replace opt #"^--\[no\]-|^--no-|^--|^-" ""))

(defn build-specs [option-defs]
  "Build option specifications"
  (if (seq option-defs)
    (loop [defs option-defs specs []]
      (if (seq defs)
        (let [[opt short doc & options] (first defs)
              spec (merge {:name (option-name opt) :long opt :short short :doc doc :parse-fn identity :multi true}
                          (apply hash-map options))]
          (recur (rest defs) (conj specs spec)))
        specs))))
  
(defn doc-for-spec [{:keys [long short doc default]}]
  [(str/join ", " [short long])
   (or doc "")
   (or (str default) "")])

(defn doc-for-specs [specs]
  (str/join "\n" (map #(str/join "\t" (doc-for-spec %)) specs)))

(defn doc-options [option-defs]
  (doc-for-specs (build-specs option-defs)))

(defn default-option-map [specs]
  "Returns an option map initalized with the default values."
  (reduce (fn [map spec] (assoc map (keyword (:name spec)) (:default spec))) {} (filter #(:default %) specs)))

(defn add-result [option-map [spec value]]
  "Add the value for an option to the option map. Handles multiple values per option."
  (let [option-key (keyword (:name spec))
        multi (true? (:multi spec))]
    (if-let [old-value (option-map option-key)]
      (cond 
        (and multi (coll? old-value)) (assoc option-map option-key (conj old-value value))
        multi (assoc option-map option-key [old-value value]))
      (assoc option-map option-key value))))

(defn parse-option-arg [specs arg]
  "Returns the option spec and the parsed value in a vector, if a matching option is found."
  (if-let [spec (first (filter (partial matches-option? arg) specs))]
    (if (long-option? arg)
      [spec ((:parse-fn spec) (str/replace arg (re-pattern (str "^" (:long spec))) ""))]
      [spec ((:parse-fn spec) (str/replace arg (re-pattern (str "^" (:short spec))) ""))])))

(defn parse-option-args [specs option-args]
  "Parses a sequence of options."
  (loop [args option-args option-map (default-option-map specs)]
    (if-let [arg (first args)]
      (if-let [result (parse-option-arg specs arg)]
        (recur (rest args) (add-result option-map result)) ; find option value and assoc [option value] to option map 
        (recur (rest args) option-map))
      option-map)))

(defn parse-args [args option-definitions]
  "Parses the args sequence into a vector of options and arguments."
  (if-let [specs (build-specs option-definitions)]
    [(filter (complement option?) args) (parse-option-args specs (filter option? args))]))
