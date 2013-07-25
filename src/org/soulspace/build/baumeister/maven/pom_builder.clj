(ns org.soulspace.build.baumeister.maven.pom-builder
  (:require [clojure.data.xml :as xml]
            [org.soulspace.build.baumeister.maven.pom-dsl :as pom])
  (:use [org.soulspace.build.baumeister.config registry]))

; TODO move to config file
(def target-to-maven-scope
  "map all targets to maven scopes"
  {"runtime" "compile"
   "dev" "test"
   "aspect" "compile"
   "aspectin" "provided"
   "model" "compile"
   "generator" "compile"
   })

;
; pom creation
;
(defn build-pom-dependency [dependency]
  (let [artifact (:artifact dependency)]
    (pom/dependency 
      {}
      (pom/groupid {} (:project artifact))
      (pom/artifactid {} (:module artifact))
      (pom/version {} (:string (:version artifact)))
      (if (not= (target-to-maven-scope (:target dependency)) "compile")
        (pom/scope {} (target-to-maven-scope (:target dependency))))
      (if (not= (:type artifact) "jar")
        (pom/type {} (:type artifact))))))

(defn build-pom-dependencies [dependencies]
  (map build-pom-dependency dependencies))

(defn build-pom [dependencies]
  (xml/indent-str
    (pom/project
      {}
      (pom/modelversion {} "4.0.0")
      (pom/groupid {} (param :project))
      (pom/artifactid {} (param :module))
      (pom/version {} (param :version))
      (pom/dependencies
        {}
        (build-pom-dependencies dependencies)))))
