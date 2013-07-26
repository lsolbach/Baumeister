(ns org.soulspace.build.baumeister.maven.pom-model
  (:use [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository artifact version]))


(defn maven-scope-to-target [maven-scope]
  "Maps a maven scope to a target"
  ((param :maven-scope-to-target) maven-scope))

(defn maven-type-to-type [maven-type]
  "Maps the maven type to a type."
  ((param :maven-type-to-type) maven-type))

; FIXME hack
(defn pom-name [dependency]
  (if (nil? (:classifier dependency))
    (:artifact-id dependency)
    (str (:artifact-id dependency) "-" (:classifier dependency))))

(defn pom-exclusion-data [exclusion]
  "Creates exclusion data from a pom exclusion."
  [(:group-id exclusion) (:artifact-id exclusion)]
  )

(defn pom-dependency-data [dependency]
  "Creates dependency data from a pom dependency."
  [[(:group-id dependency) (:artifact-id dependency) (:version dependency)
    (pom-name dependency) (maven-type-to-type (:type dependency))]
   (maven-scope-to-target (:scope dependency)) (:optional dependency) 
   (map pom-exclusion-data (:exclusions dependency))])
;
; Pom protocols
;
(defprotocol Pom
  "Protocol for the maven project object model as far as it is required."
  ; TODO add functions for parent-pom handling (properties, dependencyManagement)?
  )

(defprotocol PomDependency
  "Protocol for the maven dependency."
  )

(defprotocol PomExclusion
  "Protocol for the maven dependency."
  )

(defrecord PomImpl
  [model-version group-id artifact-id version packaging parent name description url inception-year
   dependencies dependencyManagement properties parent-pom]
  )

(defrecord PomDependencyImpl
  [group-id artifact-id version type classifier scope system-path exclusions optional]
  )

(defrecord PomExclusionImpl
  [group-id artifact-id]
  )

(defn new-pom 
  ([model-version group-id artifact-id version packaging parent name description url inception-year
    dependencies dependencyManagement properties parent-pom]
    (PomImpl. model-version group-id artifact-id version packaging parent name description url inception-year
    dependencies dependencyManagement properties parent-pom))
  )

(defn new-pom-dependency
  "Create pom dependency."
  ([group-id artifact-id]
    (new-pom-dependency group-id artifact-id nil "jar" nil "compile" nil nil false))
  ([group-id artifact-id version]
    (new-pom-dependency group-id artifact-id version "jar" nil "compile" nil nil false))
  ([group-id artifact-id version type classifier scope system-path exclusions optional]
    (PomDependencyImpl. group-id artifact-id version
                        (if (nil? type) "jar" type) classifier
                        (if (nil? scope) "compile" scope) system-path exclusions
                        (if (nil? optional) false optional))))

(defn new-pom-exclusion
  "Create pom exclusion."
  ([group-id]
    (new-pom-exclusion group-id nil))
  ([group-id artifact-id]
    (PomExclusionImpl. group-id artifact-id)))
