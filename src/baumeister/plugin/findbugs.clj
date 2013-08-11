;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.findbugs
  (:use [clojure.string :only [split join]]
        [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file]
        [org.soulspace.clj.java type-conversion]
        [baumeister.utils ant-utils files checks log]
        [baumeister.config registry plugin-registry]))

(def findbugs-classpath (lib-path ["findbugs" "findbugs-ant" "asm" "asm-tree" "bcel" "dom4j" "jaxen"])) ;  ...
(ant-taskdef {:name "findbugs" :classname "edu.umd.cs.findbugs.anttask.FindBugsTask" :classpath findbugs-classpath})
(define-ant-task ant-findbugs findbugs)
(define-ant-type ant-class edu.umd.cs.findbugs.anttask.FindBugsTask$ClassLocation)

(defmethod add-nested [:baumeister.utils.ant-utils/findbugs
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

(def config
  {:params [[:findbugs-home ""]
            [:findbugs-plugin-list ""]
            [:findbugs-report-dir "${build-report-dir}/findbugs"]
            [:findbugs-output-file "${findbugs-report-dir}/findbugs.xml"]
            [:findbugs-output "xml:withMessages"]
            [:findbugs-report-level "medium"]
            [:findbugs-max-memory "256m"]
            [:findbugs-jvm-args "-Xmx${findbugs-max-memory}"]]
   :functions [[:clean findbugs-clean]
               [:init findbugs-init]
               [:analyse findbugs-analyse]]})
