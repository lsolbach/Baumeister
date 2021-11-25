;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.utils.checks
  (:require [clojure.java.io :as io]
            [org.soulspace.clj.file :as file]
            [baumeister.config.registry :as reg]
            [baumeister.config.plugin-registry :as plreg]))

(defn plugin? [plugin] (plreg/has-plugin? plugin))

(defn java? [] (plugin? "java"))
(defn aspectj? [] (plugin? "aspectj"))
(defn clojure? [] (plugin? "clojure"))

(defn unittest? [] (file/exists? (io/as-file (reg/param :java-unittest-source-path "unittest"))))
(defn integrationtest? [] (file/exists? (io/as-file (reg/param :java-integrationtest-source-path "integrationtest"))))
(defn acceptancetest? [] (file/exists? (io/as-file (reg/param :java-acceptancetest-source-path "acceptancetest"))))

(defn code-module? [] (contains? (reg/param :code-module-types) (keyword (reg/param :type))))
(defn web-module? [] (contains? (reg/param :web-module-types) (keyword (reg/param :type))))
(defn app-module? [] (contains? (reg/param :app-frontend-types) (keyword (reg/param :type))))
(defn console-module? [] (contains? (reg/param :console-frontend-types) (keyword (reg/param :type))))
(defn data-module? [] (contains? (reg/param :data-module-types) (keyword (reg/param :type))))

;(defn package-jar? [] (contains? (param :package-type) :jar))
(defn package-war? [] (contains? #{(reg/param :package-type)} :war))
(defn package-ear? [] (contains? #{(reg/param :package-type)} :ear))
