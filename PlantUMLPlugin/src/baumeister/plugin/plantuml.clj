;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.plantuml
;  (:import [])
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file]
        [baumeister.config registry]
        [baumeister.utils ant-utils files log]))

(ant-taskdef {:name "plantuml" :classname "net.sourceforge.plantuml.ant.PlantUmlTask"})
(define-ant-task ant-plantuml plantuml)

(defn plantuml-task
  []
  (ant-plantuml {:dir (param :source-dir) :output (param :plantuml-target-dir)}))

(defn plantuml-clean
  []
  (delete-file (as-file (param :report-plantuml-dir))))

(defn plantuml-init
  []
  (create-dir (as-file (param :report-unittest-dir))))

(defn plantuml-generate
  []
  (plantuml-task))

(def config
  {:params [[:plantuml-source-dir "${source-dir}"]
            [:plantuml-target-dir "${build-dir}/plantuml"]]
   :steps [[:clean plantuml-clean]
           [:init plantuml-init]
           [:generate plantuml-generate]]
   :functions []})
