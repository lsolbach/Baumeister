(ns org.soulspace.build.baumeister.plugins.findbugs
  (:use [clojure.string :only [split join]]
        [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj.lib file]
        [org.soulspace.clj.java type-conversion]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]
        [org.soulspace.build.baumeister.config registry]))

(def findbugs-classpath (lib-path ["findbugs" "findbugs-ant" "asm" "asm-tree" "bcel" "dom4j" "jaxen"])) ;  ...
(ant-taskdef {:name "findbugs" :classname "edu.umd.cs.findbugs.anttask.FindBugsTask" :classpath findbugs-classpath})
(define-ant-task ant-findbugs findbugs)
(define-ant-type ant-class edu.umd.cs.findbugs.anttask.FindBugsTask$ClassLocation)

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/findbugs
                       edu.umd.cs.findbugs.anttask.FindBugsTask$ClassLocation]
  [_ task classloc]
    (doto (.createClass task) (.setLocation (.getLocation classloc))))

(defn findbugs-task []
  (ant-findbugs {;:home "" ; TODO check if needed
                 :classpath findbugs-classpath
                 :pluginList (param :findbugs-plugin-list)
                 :sourcePath (source-path)
                 :output (param :findbugs-output)
                 :outputFile (param :findbugs-output-file)
                 :reportLevel (param :findbugs-report-level)
                 :jvmargs (param :findbugs-jvm-args)}
                (ant-class {:location (param :build-classes-dir)})))

(defn findbugs-clean []
  (log :info "cleaning findbugs...")
  (delete-dir (as-file (param :findbugs-report-dir))))

(defn findbugs-init []
  (log :info "initializing findbugs...")
  (create-dir (as-file (param :findbugs-report-dir))))

(defn findbugs-analyse []
  (log :info "analyzing code with findbugs...")
  (findbugs-task))

(defn plugin-init []
  (log :info "initializing plugin findbugs")
  (register-vars [[:findbugs-home ""]
                  [:findbugs-plugin-list ""]
                  [:findbugs-report-dir "${build-report-dir}/findbugs"]
                  [:findbugs-output-file "${findbugs-report-dir}/findbugs.xml"]
                  [:findbugs-output "xml:withMessages"]
                  [:findbugs-report-level "medium"]
                  [:findbugs-max-memory "256m"]
                  [:findbugs-jvm-args "-Xmx${findbugs-max-memory}"]])
  (register-fns [[:clean findbugs-clean]
                 [:init findbugs-init]
                 [:analyse findbugs-analyse]]))

