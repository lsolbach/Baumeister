(ns org.soulspace.build.baumeister.repository.artifact
  (:use [clojure.string :only [split]]
        [org.soulspace.clj string]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils version]))

; Part of the artifact: project module version name type
(defprotocol Artifact
  (artifact-name [artifact])
  (artifact-name-version [artifact])
  (artifact-key [artifact])
  (artifact-module-key [artifact])
  (artifact-module-version-key [artifact])
  )

(defprotocol ArtifactPattern
  (matches-artifact? [p a])
  )

(defrecord ArtifactImpl [project module version name type]
  Artifact
  (artifact-name [artifact]
    (str (:name artifact) "." (:type artifact)))
  
  (artifact-name-version [artifact]
    (str (:name artifact) "." (:type artifact) "[" (:version artifact) "]"))
  
  (artifact-key [artifact]
    (str (:project artifact) "/" (:module artifact) "/" (:version artifact) "/"
         (:name artifact) "." (:type artifact)))
  
  (artifact-module-key [artifact]
    (str (:project artifact) "/" (:module artifact)))
  
  (artifact-module-version-key [artifact]
    (str (:project artifact) "/" (:module artifact) "/" (:version artifact)))
  )

; TODO multi method?
(defn create-artifact
  ([project module version]
    (ArtifactImpl. project module version module "jar"))
  ([project module version name]
    (ArtifactImpl. project module version name "jar"))
  ([project module version name type]
    (ArtifactImpl. project module version name type)))

; A 'nil' in a field means unspecified for an artifact pattern
; (e.g. a 'nil' version in the pattern will match every version)
(defn create-artifact-pattern
  ([project module]
    (ArtifactImpl. project module nil module nil))
  ([project module version]
    (ArtifactImpl. project module version module nil))
  ([project module version name]
    (ArtifactImpl. project module version name nil))
  ([project module version name type]
    (ArtifactImpl. project module version name type)))


(defmulti new-artifact type)

(defmethod new-artifact String [p]
  (apply create-artifact (split p #"(/|:|;|,)")))

(defmethod new-artifact clojure.lang.IPersistentVector [p]
  (apply create-artifact p))

(defmethod new-artifact clojure.lang.ISeq [p]
  (println p)
  (apply create-artifact p))


(defmulti new-artifact-pattern type)

(defmethod new-artifact-pattern String [p]
  (apply create-artifact-pattern (split p #"(/|:|;|,)")))

(defmethod new-artifact-pattern clojure.lang.IPersistentVector [p]
  (apply create-artifact-pattern p))

(defmethod new-artifact-pattern clojure.lang.ISeq [p]
  (apply create-artifact-pattern p))



;
;
;
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
    (identifier-match? (:name artifact-pattern) (:name artifact))
    (type-match? (:type artifact-pattern) (:type artifact))
    (identifier-match? (:scope artifact-pattern) (:scope artifact))))
