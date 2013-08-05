;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.maven.pom-model
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]
            [org.soulspace.build.baumeister.maven.pom-dsl :as pom])
  (:use [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils log property xml]))

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


;
; pom parsing
;
(defn parse-pom-properties [zipped]
  "create property map for pom properties"
  ; zip to properties if any and return a map of child element names and their contents
  (if-let [properties (zx/xml1-> zipped :properties)]
    (if-let [props (zip/children properties)]
      (loop [ps props property-map {}]
        (if (seq ps)
          (recur (rest ps) (assoc property-map (:tag (first ps)) (first (:content (first ps)))))
          property-map))
      {})
    {}))

(defn parse-pom-exclusion [prop-map exclusion]
  "Returns the exclusion data of a POM dependency."
  (let [e ((juxt
             #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text)))
            exclusion)]
    (apply new-pom-exclusion e)))

(defn parse-pom-dependency [prop-map dep]
  "Returns the dependency data of a POM dependency."
  (let [group-id (replace-properties prop-map (zx/xml1-> dep :groupId zx/text))
        artifact-id (replace-properties prop-map (zx/xml1-> dep :artifactId zx/text))
        version (replace-properties prop-map (zx/xml1-> dep :version zx/text))
        type (replace-properties prop-map (zx/xml1-> dep :type zx/text))
        classifier (replace-properties prop-map (zx/xml1-> dep :classifier zx/text))
        scope (replace-properties prop-map (zx/xml1-> dep :scope zx/text))
        system-path (replace-properties prop-map (zx/xml1-> dep :systemPath zx/text))
        exclusions (map (partial parse-pom-exclusion prop-map) (zx/xml-> dep :exclusions :exclusion))
        optional (replace-properties prop-map (zx/xml1-> dep :optional zx/text))
        ]
    (new-pom-dependency group-id artifact-id version type classifier scope system-path exclusions optional)))

(defn parse-pom-parent [prop-map zipped]
  "Returns the parent POM artifact data if it exists."
  (if-let [parent (zx/xml1-> zipped :parent)]
    (let [p ((juxt
             #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :version zx/text)))
            parent)]
      p)))

(defn parse-pom
  ([zipped]
    (parse-pom zipped nil))
  ([zipped parent-pom]
    "Returns the relevant data of the POM."
    (let [; merge parent properties with properties from pom
          prop-map (merge (:properties parent-pom) (parse-pom-properties zipped))
          ; merge properties with common project properties
          p-map (merge prop-map {:project.groupId (replace-properties prop-map (zx/xml1-> zipped :groupId zx/text)
                                                                       (:project.groupId prop-map))
                                 :project.artifactID (replace-properties prop-map (zx/xml1-> zipped :artifactId zx/text)
                                                                          (:project.artifactID prop-map))
                                 :project.version (replace-properties prop-map (zx/xml1-> zipped :version zx/text)
                                                                       (:project.version prop-map))
                                 :pom.groupId (replace-properties prop-map (zx/xml1-> zipped :groupId zx/text)
                                                                   (:pom.groupId prop-map))
                                 :pom.artifactID (replace-properties prop-map (zx/xml1-> zipped :artifactId zx/text)
                                                                      (:pom.artifactID prop-map))
                                 :pom.version (replace-properties prop-map (zx/xml1-> zipped :version zx/text)
                                                                       (:pom.version prop-map))})
          ; read pom data with property replacement
          model-version (replace-properties p-map (zx/xml1-> zipped :modelVersion zx/text))
          group-id (replace-properties p-map (zx/xml1-> zipped :groupId zx/text))
          artifact-id (replace-properties p-map (zx/xml1-> zipped :artifactId zx/text))
          version (replace-properties p-map (zx/xml1-> zipped :version zx/text))
          packaging (replace-properties p-map (zx/xml1-> zipped :packaging zx/text))
          parent (parse-pom-parent p-map zipped)
          name (replace-properties p-map (zx/xml1-> zipped :name zx/text))
          description (replace-properties p-map (zx/xml1-> zipped :description zx/text))
          url (replace-properties p-map (zx/xml1-> zipped :url zx/text))
          inception-year (replace-properties p-map (zx/xml1-> zipped :inceptionYear zx/text))
          dependencies (map (partial parse-pom-dependency p-map) (zx/xml-> zipped :dependencies :dependency))
          dependencyManagement (map (partial parse-pom-dependency p-map)
                                    (zx/xml-> zipped :dependencyManagement :dependencies :dependency))
          ]
      (new-pom model-version group-id artifact-id version packaging parent name description url inception-year
               dependencies dependencyManagement p-map parent-pom))))
  
;TODO use prop-map from parent pom
(defn pom-parent? [zipped]
  (not (nil? (zx/xml1-> zipped :parent))))

(defn pom-parent-data [prop-map zipped]
  (parse-pom-parent (parse-pom-properties prop-map zipped) zipped))

(defn pom-dependencies-data
  "Returns the dependencies data of the POM."
  [pom]
  (let [dependencies (:dependencies pom)]
    (map pom-dependency-data dependencies)))
