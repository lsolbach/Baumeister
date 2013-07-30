(ns org.soulspace.build.baumeister.utils.checks
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file string]
        [org.soulspace.build.baumeister.config registry parameter-registry plugin-registry]))

(defn plugin? [plugin] (has-plugin? plugin))

(defn java? [] (plugin? "java"))
(defn aspectj? [] (plugin? "aspectj"))
(defn clojure? [] (plugin? "clojure"))

(defn unittest? [] (exists? (as-file (param :java-unittest-source-path "unittest"))))
(defn integrationtest? [] (exists? (as-file (param :java-integrationtest-source-path "integrationtest"))))
(defn acceptancetest? [] (exists? (as-file (param :java-acceptancetest-source-path "acceptancetest"))))

(defn code-module? [] (contains? (param :code-module-types) (keyword (param :type))))
(defn web-module? [] (contains? (param :web-module-types) (keyword (param :type))))
(defn app-module? [] (contains? (param :app-module-types) (keyword (param :type))))
(defn console-module? [] (contains? (param :console-module-types) (keyword (param :type))))
(defn data-module? [] (contains? (param :data-module-types) (keyword (param :type))))
