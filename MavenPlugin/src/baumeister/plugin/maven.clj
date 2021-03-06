;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.maven
  (:require [org.soulspace.clj.maven.pom-dsl :as pom])
  (:use [clojure.data.xml]
        [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file]
        [org.soulspace.clj.xml zip]
        [baumeister.config registry]
        [baumeister.utils log]
        [baumeister.dependency dependency]))

; maven support plugin
(def target-to-maven-scope
  "map all targets to maven scopes"
  {:runtime "compile"
   :dev "test"
   :aspect "compile"
   :aspectin "provided"
   :model "compile"
   :generator "compile"})

;
; pom creation
;
; TODO don't create the xml directly, create a Pom model and ask for the xml
;
(defn build-pom-dependency [dependency]
  (let [artifact (:artifact dependency)]
    (pom/dependency 
      {}
      (pom/group-id {} (:project artifact))
      (pom/artifact-id {} (:module artifact))
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
      (pom/model-version {} "4.0.0")
      (pom/group-id {} (param :project))
      (pom/artifact-id {} (param :module))
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
   :steps [[:clean maven-clean]
           [:init maven-init]
           [:post-dependencies maven-post-dependencies]]
   :functions []})
