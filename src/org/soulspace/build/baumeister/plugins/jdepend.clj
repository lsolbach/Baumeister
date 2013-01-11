(ns org.soulspace.build.baumeister.plugins.jdepend
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj.lib file]
        [org.soulspace.clj.java type-conversion]
        [org.soulspace.build.baumeister.utils checks ant-utils log]
        [org.soulspace.build.baumeister.config registry])
  (:import [java.util Set]))

;(define-ant-task ant-jdepend jdepend)

(defmethod coerce [org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask$FormatAttribute String] [_ str]
  (org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask$FormatAttribute/getInstance
    org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask$FormatAttribute str))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/jdepend Set]
  [_ task exclude-set]
  (doseq [entry exclude-set]
    (doto (.createExclude task) (.setName entry))))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/jdepend org.apache.tools.ant.types.Path]
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

(defn plugin-init []
  (log :info "initializing plugin jdepend")
  (register-vars [[:jdepend-report-dir "${build-report-dir}/jdepend"]
                  [:jdepend-excludes #{"java.*" "javax.*" }]])
  (register-fns [[:clean jdepend-clean]
                 [:init jdepend-init]
                 [:analyse jdepend-analyse]]))
