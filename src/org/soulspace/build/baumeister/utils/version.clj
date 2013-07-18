(ns org.soulspace.build.baumeister.utils.version
  (:use [clojure.string :only [split]]))

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

(defn version-match? [pattern version]
  "match a version against a version pattern"
  (or (nil? pattern)
      (empty? pattern)
      ; TODO use matching
      (= pattern version)))

(defprotocol Version
  "Protocol for versions of artifacts."
  (compare-version [v1 v2] "split versions and compare components until difference or equality is established.")
  (lesser-version? [v1 v2] "Returns true if the first version is less than the second version.")
  (greater-version? [v1 v2] "Returns true if the first version is greater than the second version.")
  (same-version? [v1 v2] "Returns true if both versions are equal."))

(defprotocol VersionPattern
  )

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
    
  (lesser-version? [v1 v2]
    (< (compare-version v1 v2) 0))

  (greater-version? [v1 v2]
    (> (compare-version v1 v2) 0))

  (same-version? [v1 v2]
    (= (compare-version v1 v2) 0)))

(defprotocol VersionRange
  "Definition of a version range by two versions, from and to. [from, to["
  (contains-version? [range version]))

(defrecord VersionRangeImpl [from to]
  VersionRange
  (contains-version? [range version]
    (cond
      (nil? version) false
      (and (nil? (:from range)) (nil? (:to range))) true
      (and (nil? (:from range)) (lesser-version? version (:to range))) true
      (and (nil? (:to range)) (not (lesser-version? version (:from range)))) true
      (and (not (lesser-version? version (:from range))) (lesser-version? version (:to range))) true
      :default false)))
  

(defn new-version [string]
  (VersionImpl. string))

(defn new-version-range
  ([]
    (VersionRangeImpl. nil nil))
  ([from]
    (VersionRangeImpl. from nil))
  ([from to]
    (VersionRangeImpl. from to)))
