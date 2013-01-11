(ns org.soulspace.build.baumeister.utils.maven-utils
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj.lib file file-search function string]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils log artifact property])
  (:import [org.soulspace.build.baumeister.utils.artifact Artifact]))

(def target-to-scope
  ; TODO map all maven scopes
  [["runtime" "compile"]
   ])

(defn scope-to-target [scope]
  ; TODO map all maven scopes
  (cond
    (= scope "compile") "runtime"
    (= scope "test") "dev"
    :default "runtime"))

;
; pom parsing
;
(defmulti pom-zipper class)
(defmethod pom-zipper String [str]
  (if (starts-with "<" str)
    (zip/xml-zip (xml/parse-str str))
    (zip/xml-zip (xml/parse (reader str)))))

(defmethod pom-zipper java.io.File [pom-file]
  (zip/xml-zip (xml/parse (reader pom-file))))

;(defmethod pom-zipper java.net.URL [pom-url]
;  (zip/xml-zip (xml/parse (reader pom-url))))

(defn pom-properties [zipped]
  "create property map for pom properties"
  ; zip to properties if any and return a map of child element names and their contents
  (if-let [properties (zx/xml1-> zipped :properties)]
    (if-let [props (zip/children properties)]
      (loop [ps props prop-map {}]
        (if (seq ps)
          (recur (rest ps) (assoc prop-map (:tag (first ps)) (first (:content (first ps)))))
          prop-map)))))

(defn pom-exclusion [prop-map exclusion]
  (let [e ((juxt
             #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :version zx/text))
             (fn [_] "exclude"))
            exclusion)]
    (println "POM ARTIFACT EXCLUSION" e)
    (log :debug "POM ARTIFACT EXCLUSION" e)
    e))

; TODO handle exclusions
; TODO Should the exclusions in the resulting tree be children of the artifacts they are children of in the pom file
; TODO or should they be promoted to exclusions of the artifact represented by the pom file itself?
(defn pom-dependency [prop-map dep]
;  (if-let [exclusion (zx/xml-> dep :exclusions :exclusion)]
;    (println "Excludes" (map (partial pom-exclusion prop-map) exclusion)))
  (let [a ((juxt
             #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :version zx/text))
             #(scope-to-target (zx/xml1-> % :scope zx/text)))
            dep)]
    (log :debug "POM ARTIFACT" a)
    a))

(defn pom-dependencies [pom]
  ; Returns a sequence of artifacts?
  (let [zipped (pom-zipper pom)
        prop-map (pom-properties zipped)
        deps (zx/xml-> zipped :dependencies :dependency)
        excls (zx/xml-> zipped :dependencies :dependency :exclusions :exclusion)]
;    (println "EXATYPE:" (type excls))
;    (println "EXA:" (map #(zx/xml1-> % :artifactId zx/text) excls))
    (try
      ; TODO don't simply map pom-artifacts,
      ; TODO convert the more complex structure of dependencies, properties (e.g.) and exclusions for the dependencies
      ;(println "EXB:" (map (partial pom-exclusion prop-map) excls))
      (map (partial pom-dependency prop-map) deps);(zx/xml-> zipped :dependencies :dependency))
      (catch Exception e
        (log :error "Could not parse" pom (.getMessage e))))))

;
; pom creation
;
(defn build-pom-dependency-xml [artifact]
  ; TODO scope translation
    (xml/element :dependency {}
             (xml/element :groupId {} (:project artifact))
             (xml/element :artifactId {} (:module artifact))
             (xml/element :version {} (:version artifact))))

(defn build-pom-dependencies-xml []
  (apply xml/element :dependencies {}
         (map build-pom-dependency-xml (map new-artifact (param :dependencies)))))

(defn build-pom-xml []
  (xml/indent-str
    (xml/element :project {}
                 (xml/element :modelVersion {} "4.0.0")
                 (xml/element :groupId {} (param :project))
                 (xml/element :artifactId {} (param :name))
                 (xml/element :version {} (param :version))
                 (build-pom-dependencies-xml))))
