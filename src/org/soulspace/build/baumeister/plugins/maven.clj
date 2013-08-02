(ns org.soulspace.build.baumeister.plugins.maven
  (:require [org.soulspace.build.baumeister.maven.pom-dsl :as pom])
  (:use [clojure.data.xml]
        [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils log message property xml]
        [org.soulspace.build.baumeister.dependency dependency]))

; maven support plugin

(def target-to-maven-scope
  "map all targets to maven scopes"
  {:runtime "compile"
   :dev "test"
   :aspect "compile"
   :aspectin "provided"
   :model "compile"
   :generator "compile"
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

(defn build-pom []
  (let [dependencies (map #(apply new-dependency %) (param :dependencies))]
    (pom/project
      {}
      (pom/modelversion {} "4.0.0")
      (pom/groupid {} (param :project))
      (pom/artifactid {} (param :module))
      (pom/version {} (param :version))
      (pom/dependencies
        {}
        (build-pom-dependencies dependencies)))))

(defn maven-clean 
  []
  (delete-file (as-file (param :maven-build-dir))))

(defn maven-init
  []
  (create-dir (as-file (param :maven-build-dir))))

(defn maven-post-dependencies []
  (spit (param "${maven-build-dir}/pom.xml") (indent-str (build-pom))))


(def config
  {:params [[:maven-build-dir "${build-dir}/maven"]]
   :functions [[:clean maven-clean]
               [:init maven-init]
               [:post-dependencies maven-post-dependencies]]})

(defn plugin-init []
  (register-vars (:params config))
  (register-fns (:functions config))
  )