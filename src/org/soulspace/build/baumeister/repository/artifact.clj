(ns org.soulspace.build.baumeister.repository.artifact
  (:use [clojure.string :only [split trim]]
        [org.soulspace.clj string]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository version]))

;
; Part of the artifact: project module version name type
;
(defprotocol Artifact
  (artifact-name [artifact] "Returns the name of the artifact.")
  (artifact-version [artifact])
  (artifact-name-version [artifact])
  (artifact-key [artifact])
  (artifact-module-key [artifact])
  (artifact-module-version-key [artifact]))

(defrecord ArtifactImpl [project module version name type]
  Artifact
  (artifact-name [artifact]
    (str (:name artifact) "." (:type artifact)))
  (artifact-version [artifact]
    (:string (:version artifact)))
  (artifact-name-version [artifact]
    (str (:name artifact) "." (:type artifact) "[" (artifact-version artifact) "]"))
  (artifact-key [artifact]
    (str (:project artifact) "/" (:module artifact) "/" (artifact-version artifact) "/"
         (:name artifact) "." (:type artifact)))
  (artifact-module-key [artifact]
    (str (:project artifact) "/" (:module artifact)))
  (artifact-module-version-key [artifact]
    (str (:project artifact) "/" (:module artifact) "/" (artifact-version artifact)))
  )

;
; TODO needed?
;
(defprotocol MavenArtifact
  (mvn-artifact-name [artifact] "Returns the name of the artifact in maven convention."))

(defrecord MavenArtifactImpl [project module version name type classifier]
  Artifact
  (artifact-name [artifact]
    (str (:name artifact)
         (if (seq classifier) classifier)
         "." (:type artifact)))
  (artifact-version [artifact]
    (:string (:version artifact)))
  (artifact-name-version [artifact]
    (str (:name artifact) "." (:type artifact) "[" (artifact-version artifact) "]"))
  (artifact-key [artifact]
    (str (:project artifact) "/" (:module artifact) "/" (artifact-version artifact) "/"
         (:name artifact) "." (:type artifact)))
  (artifact-module-key [artifact]
    (str (:project artifact) "/" (:module artifact)))
  (artifact-module-version-key [artifact]
    (str (:project artifact) "/" (:module artifact) "/" (artifact-version artifact)))
  MavenArtifact
  (mvn-artifact-name [artifact]
    (str name "-" (artifact-version artifact)
         (if (seq classifier) (str "-" classifier))
         "." type))
  )


(defn create-artifact
  ([project module version]
    (ArtifactImpl. project module (new-version version) module "jar"))
  ([project module version name]
    (ArtifactImpl. project module (new-version version) name "jar"))
  ([project module version name type]
    (ArtifactImpl. project module (new-version version) name type))
  ([project module version name type classifier]
    (MavenArtifactImpl. project module (new-version version) name type classifier)))


(defn matches-identifier? [pattern name]
  (or (nil? pattern)
      (empty? pattern)
      ; TODO match pattern, not just starts with
      (starts-with pattern name)))

(defn matches-type? [pattern type]  
  (or (nil? pattern)
      (empty? pattern)
      ; TODO use matching
      (= pattern type)))
;
;
;
(defprotocol ArtifactPattern
  (matches-artifact? [pattern artifact] "match an artifact pattern against an artifact")
;  (matches-identifier? [pattern artifact] "match an identifier pattern against an identifier")
;  (matches-type? [pattern artifact] "match a type pattern against a type")
  )

(defrecord ArtifactPatternImpl [project module version-range name type]
  ArtifactPattern
  (matches-artifact? [pattern artifact]
    (and 
    (matches-identifier? (:project pattern) (:project artifact))
    (matches-identifier? (:module pattern) (:module artifact))
    (contains-version? (:version-range pattern) (:version artifact))
    (matches-identifier? (:name pattern) (:name artifact))
    (matches-type? (:type pattern) (:type artifact))))
  )

; A 'nil' in a field means unspecified for an artifact pattern
; (e.g. a 'nil' module in the pattern will match every module in the project)
(defn create-artifact-pattern
  ([project module]
    (ArtifactPatternImpl. project module (new-version-range) module nil))
  ([project module range]
    (ArtifactPatternImpl. project module (new-version-range range) module nil))
  ([project module range name]
    (ArtifactPatternImpl. project module (new-version-range range) name nil))
  ([project module range name type]
    (ArtifactPatternImpl. project module (new-version-range range) name type)))


(def artifact-hierarchy (make-hierarchy))

; TODO use hierarchy
(defmulti new-artifact type)
(defmethod new-artifact String [arg] (apply create-artifact (map trim (split arg #"(/|:|;|,)"))))
(defmethod new-artifact clojure.lang.IPersistentVector [arg] (apply create-artifact arg))
(defmethod new-artifact clojure.lang.ISeq [arg] (apply create-artifact arg))
(defmethod new-artifact ArtifactImpl [arg] arg)

; TODO use hierarchy
(defmulti new-artifact-pattern type)
(defmethod new-artifact-pattern String [arg] (apply create-artifact-pattern (map trim (split arg #"(/|:|;|,)"))))
(defmethod new-artifact-pattern clojure.lang.IPersistentVector [arg] (apply create-artifact-pattern arg))
(defmethod new-artifact-pattern clojure.lang.ISeq [arg] (apply create-artifact-pattern arg))
(defmethod new-artifact-pattern ArtifactPatternImpl [arg] (apply create-artifact-pattern arg))
(defmethod new-artifact-pattern ArtifactImpl [arg]
  (create-artifact-pattern (:project arg) (:module arg) (:version arg) (:name arg) (:type arg)))
