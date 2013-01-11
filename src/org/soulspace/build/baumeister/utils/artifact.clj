(ns org.soulspace.build.baumeister.utils.artifact
  (:use [clojure.string :only [split]]
        [org.soulspace.clj.lib string]
        [org.soulspace.build.baumeister.config registry]))

(defrecord Artifact [project module version target artifact type scope])

(defn new-artifact
  ([project module version]
    (Artifact. project module version "runtime" module "jar" nil))
  ([project module version target]
    (Artifact. project module version target module "jar" nil))
  ([project module version target artifact]
    (Artifact. project module version target artifact "jar" nil))
  ([project module version target artifact type]
    (Artifact. project module version target artifact type nil))
  ([project module version target artifact type scope]
    (Artifact. project module version target artifact type scope))
  ([dep]
    ; FIXME copy fields instead of apply dep to cope with structural differences between dependencies and artifacts
    (if (instance? Artifact dep) ; TODO remove if everything works
      (throw (RuntimeException. "It's already an artifact!")))
    (apply new-artifact dep)))

; A 'nil' in a field means unspecified for an artifact pattern
; (e.g. a 'nil' version in the pattern will match every version)
(defn new-artifact-pattern
  ([project module]
    (Artifact. project module nil nil module nil nil))
  ([project module version]
    (Artifact. project module version nil module nil nil))
  ([project module version target]
    (Artifact. project module version target module nil nil))
  ([project module version target artifact]
    (Artifact. project module version target artifact nil nil))
  ([project module version target artifact type]
    (Artifact. project module version target artifact type nil))
  ([project module version target artifact type scope]
    (Artifact. project module version target artifact type scope))
  ([dep]
    ; FIXME copy fields instead of apply dep to cope with structural differences between dependencies and artifacts
    (if (instance? Artifact dep) ; TODO remove if everything works
      (throw (RuntimeException. "It's already an artifact!")))
    (apply new-artifact-pattern dep)))

(defn artifact-name [artifact]
  (str (:artifact artifact) "." (:type artifact)))

(defn artifact-name-version [artifact]
  (str (:artifact artifact) "." (:type artifact) "[" (:version artifact) "]"))

(defn artifact-key [artifact]
  (str (:project artifact) "/" (:module artifact) "/" (:version artifact) "/"
       (:artifact artifact) "." (:type artifact) "->" (:target artifact)))

(defn artifact-module-key [artifact]
  (str (:project artifact) "/" (:module artifact)))

(defn artifact-module-version-key [artifact]
  (str (:project artifact) "/" (:module artifact) "/" (:version artifact)))

; version comparison
(defn numeric? [x]
  (and (not (nil? x)) (re-matches #"^[0-9]+$" x)))

(defn compare-revision [c1 c2]
  "compare a revision"
  (if (and (numeric? c1) (numeric? c2)) ; compare numerically or lexically?
    (compare (Long/valueOf c1) (Long/valueOf c2))
    (compare c1 c2)))

(defn split-version [version]
  "split version string at dots"
  (split version #"[.]")) 

(defn compare-version [v1 v2]
  "split versions and compare components until difference or equality is established"
  (if (or (nil? v1) (nil? v2))
    (compare v1 v2)
    (loop [c1 (split-version v1)
           c2 (split-version v2)]
      (if (and (seq c1) (seq c1))
        (if (not= (first c1) (first c2))
          (compare-revision (first c1) (first c2))
          (recur (rest c1) (rest c2)))
        (compare-revision (first c1) (first c2))))))


(defn version-match? [pattern version]
  "match a version against a version pattern"
  (or (nil? pattern)
      (empty? pattern)
      ; TODO use matching
      (= pattern version)))

(defn identifier-match? [pattern name]
  "match an identifier against a pattern"
  (or (nil? pattern)
      (empty? pattern)
      ; TODO match pattern, not just starts with
      (starts-with pattern name)))

(defn type-match? [pattern type]
  "match a type against a type pattern"
  (or (nil? pattern)
      (empty? pattern)
      ; TODO use matching
      (= pattern type)))

(defn artifact-match? [artifact-pattern artifact]
  "match artifact against the artifact pattern"
  (and 
    (identifier-match? (:project artifact-pattern) (:project artifact))
    (identifier-match? (:module artifact-pattern) (:module artifact))
    (version-match? (:version artifact-pattern) (:version artifact))
    (identifier-match? (:target artifact-pattern) (:target artifact))
    (identifier-match? (:artifact artifact-pattern) (:artifact artifact))
    (type-match? (:type artifact-pattern) (:type artifact))
    (identifier-match? (:scope artifact-pattern) (:scope artifact))))
