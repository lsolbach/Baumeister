(ns org.soulspace.build.baumeister.plugins.checkstyle
  (:use [clojure.string :only [split join]]
        [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file]
        [org.soulspace.clj.java type-conversion]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

(def checkstyle-jar (str (get-lib-dir) "/checkstyle-all.jar"))
(ant-taskdef {:classpath checkstyle-jar :resource "checkstyletask.properties"})
(define-ant-task ant-checkstyle checkstyle) 
(define-ant-type checkstyle-formatter com.puppycrawl.tools.checkstyle.CheckStyleTask$Formatter) 

(defmethod coerce [com.puppycrawl.tools.checkstyle.CheckStyleTask$FormatterType String] [_ str]
  (com.puppycrawl.tools.checkstyle.CheckStyleTask$FormatterType/getInstance
    com.puppycrawl.tools.checkstyle.CheckStyleTask$FormatterType str))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/checkstyle
                       com.puppycrawl.tools.checkstyle.CheckStyleTask$Formatter]
  [_ task formatter] (.addFormatter task formatter))

(defn checkstyle-task []
  (ant-checkstyle {:config (param :checkstyle-config)
                   :failOnViolation (param :checkstyle-fail-on-violation)}
                  (checkstyle-formatter {:type "xml" :tofile (param :checkstyle-report-file)})
                  (ant-fileset {:dir (param :module-dir) :includes (join " " (split (source-path) #":"))})))

(defn checkstyle-clean []
  (log :info "cleaning checkstyle...")
  (delete-dir (as-file (param :checkstyle-report-dir))))

(defn checkstyle-init []
  (log :info "initializing checkstyle...")
  (create-dir (as-file (param :checkstyle-report-dir))))

(defn checkstyle-analyse []
  (log :info "analyzing code with checkstyle...")
  (checkstyle-task))

(def checkstyle-config
  {:params [[:checkstyle-config (str (param :baumeister_home_dir) "/config/checkstyle/sun_checks.xml")]
            [:checkstyle-report-dir "${build-report-dir}/checkstyle"]
            [:checkstyle-report-file "${checkstyle-report-dir}/checkstyle.xml"]
            [:checkstyle-fail-on-violation "false"]]
   :functions [[:clean checkstyle-clean]
               [:init checkstyle-init]
               [:analyse checkstyle-analyse]]})

(defn plugin-init []
  (log :info "initializing plugin checkstyle")
  (register-vars (:params checkstyle-config))
  (register-fns (:functions checkstyle-config)))
