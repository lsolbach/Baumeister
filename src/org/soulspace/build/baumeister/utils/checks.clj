;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
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
(defn app-module? [] (contains? (param :app-frontend-types) (keyword (param :type))))
(defn console-module? [] (contains? (param :console-frontend-types) (keyword (param :type))))
(defn data-module? [] (contains? (param :data-module-types) (keyword (param :type))))

;(defn package-jar? [] (contains? (param :package-type) :jar))
(defn package-war? [] (contains? #{(param :package-type)} :war))
(defn package-ear? [] (contains? #{(param :package-type)} :ear))
