(ns org.soulspace.build.baumeister.utils.maven-utils
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]
            [org.soulspace.build.baumeister.utils.pom-dsl :as pom])
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function string]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.dependency dependency]
        [org.soulspace.build.baumeister.utils log property]))

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
  (let [artifact ((juxt
                    #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
                    #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text))
                    #(replace-properties prop-map (zx/xml1-> % :version zx/text)))
                     dep)
        target (scope-to-target (zx/xml1-> dep :scope zx/text))
        exclusions (map pom-exclusion (zx/xml-> dep :exclusions :exclusion))]
    (log :debug "POM ARTIFACT" artifact "->" exclusions)
    [artifact target exclusions]))

(defn pom-dependencies [pom]
  ; Returns a sequence of artifacts?
  (let [zipped (pom-zipper pom)
        prop-map (pom-properties zipped)
        deps (zx/xml-> zipped :dependencies :dependency)]
    (try
      (map (partial pom-dependency prop-map) deps);(zx/xml-> zipped :dependencies :dependency))
      (catch Exception e
        (log :error "Could not parse" pom (.getMessage e))))))

;
; pom creation
;
(defn build-pom-dependency [dependency]
  (let [artifact (:artifact dependency)]
    (pom/dependency 
      {}
      (pom/groupid {} (:project artifact))
      (pom/artifactid {} (:module artifact))
      (pom/version {} (artifact-version artifact))
      ; TODO scope translation if needed
    )))

(defn build-pom-dependencies []
  (map build-pom-dependency (map new-dependency (param :dependencies))))

(defn build-pom []
  (xml/indent-str
    (pom/project
      {}
      (pom/modelversion {} "4.0.0")
      (pom/groupid {} (param :project))
      (pom/artifactid {} (param :module))
      (pom/version {} (param :version))
      (pom/dependencies
        {}
        (build-pom-dependencies)))))
  