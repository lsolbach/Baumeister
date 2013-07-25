(ns org.soulspace.build.baumeister.maven.maven-utils
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function string]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.dependency dependency]
        [org.soulspace.build.baumeister.utils log property]
        [org.soulspace.build.baumeister.maven pom-model]))

;
; pom parsing
;
(defmulti pom-zipper class)
(defmethod pom-zipper java.io.File [pom-file]
  (zip/xml-zip (xml/parse (reader pom-file))))
(defmethod pom-zipper String [str]
  (if (starts-with "<" str)
    (zip/xml-zip (xml/parse-str str)) ; handle as xml string
    (zip/xml-zip (xml/parse (reader str))))); haandle as file name

(defn parse-pom-properties [prop-map zipped]
  "create property map for pom properties"
  ; zip to properties if any and return a map of child element names and their contents
  (if-let [properties (zx/xml1-> zipped :properties)]
    (if-let [props (zip/children properties)]
      (loop [ps props property-map {}]
        (if (seq ps)
          (recur (rest ps) (assoc property-map (:tag (first ps)) (first (:content (first ps)))))
          property-map)))))

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

(defn parse-pom [prop-map zipped]
  (let [pom-prop-map (parse-pom-properties prop-map zipped)
        properties (merge prop-map pom-prop-map)
        model-version (replace-properties properties (zx/xml1-> zipped :modelVersion zx/text))
        group-id (replace-properties properties (zx/xml1-> zipped :groupId zx/text))
        artifact-id (replace-properties properties (zx/xml1-> zipped :artifactId zx/text))
        version (replace-properties properties (zx/xml1-> zipped :version zx/text))
        packaging (replace-properties properties (zx/xml1-> zipped :packaging zx/text))
        parent (parse-pom-parent properties zipped)
        name (replace-properties properties (zx/xml1-> zipped :modelVersion zx/text))
        description (replace-properties properties (zx/xml1-> zipped :modelVersion zx/text))
        url (replace-properties properties (zx/xml1-> zipped :modelVersion zx/text))
        inception-year (replace-properties properties (zx/xml1-> zipped :modelVersion zx/text))
        dependencies (map (partial parse-pom-dependency properties) (zx/xml-> zipped :dependencies :dependency))
        dependencyManagement (map (partial parse-pom-dependency properties)
                                  (zx/xml-> zipped :dependencyManagement :dependencies :dependency))]
    (new-pom model-version group-id artifact-id version packaging parent name description url inception-year
             dependencies dependencyManagement properties)))

(defn pom-parent? [zipped]
  (not (nil? (zx/xml1-> zipped :parent))))

(defn pom-parent-data [prop-map zipped]
  (parse-pom-parent prop-map zipped))

(defn pom-dependencies-data
  "Returns the dependencies data of the POM."
  [pom]
  (let [dependencies (:dependencies pom)]
    (println dependencies)
    (map pom-dependency-data dependencies)))
