;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.jdepend
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file]
        [org.soulspace.clj.java type-conversion]
        [baumeister.utils checks ant-utils log]
        [baumeister.config registry plugin-registry])
  (:import [java.util Set]))

(define-ant-task ant-jdepend jdepend)

(defmethod coerce [org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask$FormatAttribute String] [_ str]
  (org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask$FormatAttribute/getInstance
    org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask$FormatAttribute str))

(defmethod add-nested [:baumeister.utils.ant-utils/jdepend Set]
  [_ task exclude-set]
  (doseq [entry exclude-set]
    (doto (.createExclude task) (.setName entry))))

(defmethod add-nested [:baumeister.utils.ant-utils/jdepend org.apache.tools.ant.types.Path]
  [_ task path]
  (doto  (.createClassespath task) (.add path)))

(defn get-classpath []
  (cond
    (aspectj?) (param :aspectj-class-path)
    (java?) (param :java-class-path)
    (clojure?) (param :clojure-class-path)))

(defn jdepend-clean []
    (delete-file (as-file (param :jdepend-report-xml-dir)))
    (delete-file (as-file (param :jdepend-report-dir))))

(defn jdepend-init []
  (create-dir (as-file (param :jdepend-report-dir)))
  (create-dir (as-file (param :jdepend-report-xml-dir))))

(defn jdepend-analyse []
  (ant-jdepend {:classpath (get-classpath) :format "xml" :outputFile (param "${jdepend-report-dir}/jdepend.xml")}
               (ant-path (param :build-classes-dir))
               (param :jdepend-excludes)))

(def config
  {:params [[:jdepend-report-dir "${build-report-dir}/jdepend"]
            [:jdepend-excludes #{"java.*" "javax.*" }]]
   :functions [[:clean jdepend-clean]
               [:init jdepend-init]
               [:analyse jdepend-analyse]]})
