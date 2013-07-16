(ns org.soulspace.build.baumeister.dependency.artifact
  (:use [org.soulspace.clj string]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils version]))

; TODO define artifact protocol and include the artifact-* functions
; TODO let ArtifactImpl implement the artifact protocol

; Part of the artifact: project module version artifact type
; Part of the dependency: target scope exclusions
; TODO rename parameter artifact to name
(defrecord Artifact [project module version artifact type])



(defn new-artifact
  ([project module version]
    (Artifact. project module version module "jar"))
  ([project module version artifact]
    (Artifact. project module version artifact "jar"))
  ([project module version artifact type]
    (Artifact. project module version artifact type))
  ([art]
    ; TODO apply new artifact anyway to create a copy?
    (println "new-artifact:" art)
    (if (instance? Artifact art) ; TODO remove if everything works
      (throw (RuntimeException. "It's already an artifact!")))
    (apply new-artifact art)))



; A 'nil' in a field means unspecified for an artifact pattern
; (e.g. a 'nil' version in the pattern will match every version)
(defn new-artifact-pattern
  ([project module]
    (Artifact. project module nil module nil))
  ([project module version]
    (Artifact. project module version module nil))
  ([project module version artifact]
    (Artifact. project module version artifact nil))
  ([project module version artifact type]
    (Artifact. project module version artifact type))
  ([arti]
    ; TODO apply new artifact anyway to create a copy?
    (if (instance? Artifact arti) ; TODO remove if everything works
      (throw (RuntimeException. "It's already an artifact!")))
    (apply new-artifact-pattern arti)))


(defn artifact-name [artifact]
  (str (:artifact artifact) "." (:type artifact)))

(defn artifact-name-version [artifact]
  (str (:artifact artifact) "." (:type artifact) "[" (:version artifact) "]"))

(defn artifact-key [artifact]
  (str (:project artifact) "/" (:module artifact) "/" (:version artifact) "/"
       (:artifact artifact) "." (:type artifact)))

(defn artifact-module-key [artifact]
  (str (:project artifact) "/" (:module artifact)))

(defn artifact-module-version-key [artifact]
  (str (:project artifact) "/" (:module artifact) "/" (:version artifact)))

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
