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

