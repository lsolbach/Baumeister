(ns org.soulspace.build.baumeister.maven.pom-model
  (:require [org.soulspace.build.baumeister.maven.pom-dsl :as pom])
  (:use [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]))

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
  (pom-xml [pom] "Builds the XML for this POM.")
  )

(defprotocol PomParent
  "Protocol for the POM parent."
  (parent-xml [pom] "Builds the XML for this POM parent.")
  )

(defprotocol PomDependency
  "Protocol for the POM dependency."
  (dependency-xml [pom] "Builds the XML for this POM dependency.")
  )

(defprotocol PomExclusion
  "Protocol for the POM exclusion."
  (exclusion-xml [pom] "Builds the XML for this POM exclusion.")
  )

(defrecord PomImpl
  [model-version group-id artifact-id version packaging parent name description url inception-year
   dependencies dependencyManagement properties parent-pom]
  Pom
  (pom-xml [this] 
    (pom/project {}
                 (when (:model-version this) (pom/modelversion {} (:model-version this)))
                 (when (:group-id this) (pom/groupid {} (:group-id this)))
                 (pom/artifactid {} (:artifact-id this))
                 (when (:version this) (pom/version {} (:version this)))
                 (when (:name this) (pom/name {} (:name this)))
                 (when (:description this) (pom/description {} (:description this)))
                 (when (:inception-year this) (pom/inceptionyear {} (:inception-year this)))
                 (when (:url this) (pom/name {} (:url this)))
                 (when (:parent this) (parent-xml (:parent this)))
                 (when (seq (:dependencies this))
                   (pom/dependencies {} (map dependency-xml (:dependencies this))))
                 (when (seq (:dependencyManagement this))
                   (pom/dependencymanagement {} (map dependency-xml (:dependencies this))))
                 (when (seq (:properties this))
                   (pom/properties {} ))
                 )))

(defrecord PomParentImpl
  [group-id artifact-id version]
  PomParent
  (parent-xml [this] 
    (pom/parent {}
                (pom/groupid {} (:group-id this))
                (pom/artifactid {} (:artifact-id this))
                (pom/version {} (:version this))))
  )
  
(defrecord PomDependencyImpl
  [group-id artifact-id version type classifier scope system-path exclusions optional]
  PomDependency
  (dependency-xml [this] 
    (pom/dependency {}
                    (pom/groupid {} (:group-id this))
                    (pom/artifactid {} (:artifact-id this))
                    (when (:version this) (pom/version {} (:version this)))
                    (when (:type this) (pom/type {} (:type this)))
                    (when (:classifier this) (pom/classifier {} (:classifier this)))
                    (when (:scope this) (pom/scope {} (:scope this)))
                    (when (:system-path this) (pom/systempath {} (:system-path this)))
                    (when (:optional this) (pom/optional {} (:optional this)))
                    (when (seq (:exclusions this)
                               (pom/exclusions {} (:exclusions this))))))
  )

(defrecord PomExclusionImpl
  [group-id artifact-id]
  PomExclusion
  (exclusion-xml [this] 
    (pom/exclusion {}
                 (when (:group-id this) (pom/groupid {} (:group-id this)))
                 (when (:artifact-id this) (pom/artifactid {} (:artifact-id this)))))
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
