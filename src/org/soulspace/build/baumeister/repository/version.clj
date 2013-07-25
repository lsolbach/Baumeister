(ns org.soulspace.build.baumeister.repository.version
  (:use [clojure.string :only [split]]))

;
; version comparison utils
;
(defn digits-only? [x]
  "test if x contains digits only"
  (and (not (nil? x)) (re-matches #"^[0-9]+$" x)))

(defn compare-revision [c1 c2]
  "compare a revision"
  (if (and (digits-only? c1) (digits-only? c2)) ; compare numerically or lexically?
    (compare (Long/valueOf c1) (Long/valueOf c2))
    (compare c1 c2)))

(defn split-version 
  ([version]
    "split version string at dots"
    (split version #"[.]"))
  ([version re]
    "split version string at regular expression"
    (split version re))
  ) 

; TODO still needed?
(defn version-match? [pattern version]
  "match a version against a version pattern"
  (or (nil? pattern)
      (empty? pattern)
      ; TODO use matching
      (= pattern version)))

;
;
;
(defprotocol Version
  "Protocol for a version (of an artifact)."
  (compare-version [v1 v2] "split versions and compare components until difference or equality is established.")
  (lesser-version? [v1 v2] "Returns true if the first version is less than the second version.")
  (greater-version? [v1 v2] "Returns true if the first version is greater than the second version.")
  (same-version? [v1 v2] "Returns true if both versions are equal.")
  (unset-version? [v] "Returns true, if the version is not set (nil or empty string)."))

(defprotocol IncrementableVersion
  (inc-version [v] "Increases the current version by one increment."))

(defprotocol VersionRange
  "Protocol for a version range defined by two versions, from and to.
  The from version is included in the range, the to version is excluded. ([from, to[)"
  (contains-version? [range version]))

(defrecord VersionImpl [string]
  Version
  (compare-version [version1 version2]
    (let [v1 (:string version1)
          v2 (:string version2)]
      (if (or (nil? v1) (nil? v2))
        (compare v1 v2)
        (loop [c1 (split-version v1)
               c2 (split-version v2)]
          (if (and (seq c1) (seq c1))
            (if (not= (first c1) (first c2))
              (compare-revision (first c1) (first c2))
              (recur (rest c1) (rest c2)))
            (compare-revision (first c1) (first c2)))))))
    
  (lesser-version? [v1 v2] (< (compare-version v1 v2) 0))
  (greater-version? [v1 v2] (> (compare-version v1 v2) 0))
  (same-version? [v1 v2] (= (compare-version v1 v2) 0))
  (unset-version? [v] (empty? (:string v)))
  VersionRange
  (contains-version? [v1 v2] (same-version? v1 v2))
  )

(defn create-version [string]
  (VersionImpl. string))

(defmulti new-version type)
(defmethod new-version String [v] (create-version v))
(defmethod new-version VersionImpl [v] v)
(defmethod new-version nil [v] (create-version nil))

(defrecord VersionRangeImpl [from to]
  VersionRange
  (contains-version? [range version]
    (cond
      (or (nil? version) (unset-version? version)) false
      (and (or (nil? (:from range)) (unset-version? (:from range))) (or (nil? (:to range)) (unset-version? (:to range)))) true
      (and (or (nil? (:from range)) (unset-version? (:from range))) (lesser-version? version (:to range))) true
      (and (or (nil? (:to range)) (unset-version? (:to range))) (not (lesser-version? version (:from range)))) true
      (and (not (lesser-version? version (:from range))) (lesser-version? version (:to range))) true
      :default false)))

(defn create-version-range
  ([]
    (VersionRangeImpl. nil nil))
  ([from]
    (VersionRangeImpl. (new-version from) nil))
  ([from to]
    (VersionRangeImpl. (new-version from) (new-version to))))

(defmulti new-version-range1 type)
(defmethod new-version-range1 String [arg] (create-version-range arg))
(defmethod new-version-range1 clojure.lang.IPersistentVector [[from to] arg] (create-version-range from to))
(defmethod new-version-range1 clojure.lang.ISeq [[from to] arg] (create-version-range from to))
(defmethod new-version-range1 VersionImpl [arg] arg)
(defmethod new-version-range1 VersionRangeImpl [arg] arg)
(defmethod new-version-range1 nil [arg] (create-version-range nil))

(defn new-version-range
  ([]
    (create-version-range))
  ([arg]
    (new-version-range1 arg))
  ([from to]
    (create-version-range from to)))
