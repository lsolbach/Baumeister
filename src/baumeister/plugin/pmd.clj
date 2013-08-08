;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.pmd
  (:use [clojure.string :only [split join]]
        [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file]
        [org.soulspace.clj.java type-conversion]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

(def pmd-jar (str (get-lib-dir) "/pmd.jar"))

(ant-taskdef {:name "pmd" :classname "net.sourceforge.pmd.ant.PMDTask" :classpath pmd-jar})
(define-ant-task ant-pmd pmd)
(define-ant-type pmd-formatter net.sourceforge.pmd.ant.Formatter)
;(define-ant-type pmd-ruleset net.sourceforge.pmd.ant.RuleSetWrapper)

(ant-taskdef {:name "cpd" :classname "net.sourceforge.pmd.cpd.CPDTask" :classpath pmd-jar})
(define-ant-task ant-cpd cpd)

(defmethod coerce [net.sourceforge.pmd.cpd.CPDTask$FormatAttribute String] [_ str]
  (net.sourceforge.pmd.cpd.CPDTask$FormatAttribute/getInstance
    net.sourceforge.pmd.cpd.CPDTask$FormatAttribute str))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/pmd net.sourceforge.pmd.ant.Formatter]
  [_ task formatter] (.addFormatter task formatter))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/pmd java.util.List]
  [_ task rulesets]
  (doseq [ruleset rulesets]
    (.addRuleset task (doto (net.sourceforge.pmd.ant.RuleSetWrapper.) (.addText ruleset)))))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/pmd net.sourceforge.pmd.ant.RuleSetWrapper]
  [_ task ruleset] (.addRuleset task ruleset))


(defn pmd-task []
  (apply ant-pmd {:shortFilenames "true" :encoding "UTF-8"
                  :ruleSetFiles (join "," (map #(str "rulesets/" % ".xml") (param :pmd-rule-sets)))}
         (pmd-formatter {:type "xml" :toFile (param :pmd-report-file)})
         (map #(ant-fileset {:dir % :includes (param :pmd-source-pattern)}) (split (source-path) #":"))))

(defn cpd-task []
  (apply ant-cpd {:minimumTokenCount (param :cpd-minimum-token-count)
                  :format "xml" :encoding "UTF-8" :outputFile (str (param :cpd-report-dir) "/cpd.xml")}
           (map #(ant-fileset {:dir % :includes (param :cpd-source-pattern)}) (split (source-path) #":"))))

(defn pmd-clean []
  (log :debug  "cleaning pmd...")
  (delete-dir (as-file (param :pmd-report-dir)))
  (delete-dir (as-file (param :cpd-report-dir))))

(defn pmd-init []
  (log :debug  "initializing pmd...")
  (create-dir (as-file (param :pmd-report-dir)))
  (create-dir (as-file (param :cpd-report-dir))))

(defn pmd-analyse []
  (log :debug  "analyzing code with pmd and cpd...")
  (cpd-task)
  (pmd-task))

(def config
  {:params [[:pmd-report-dir "${build-report-dir}/pmd"]
            [:pmd-report-file "${pmd-report-dir}/pmd.xml"]
            [:pmd-rule-sets ["java/basic" "java/braces" "java/unusedcode"]]
            [:pmd-source-pattern "**/*.java,**/*.aj,**/*.clj"]
            [:cpd-report-dir "${build-report-dir}/cpd"]
            [:cpd-source-pattern "**/*.java,**/*.aj,**/*.clj"]
            [:cpd-minimum-token-count 50]]
   :functions [[:clean pmd-clean]
               [:init pmd-init]
               [:analyse pmd-analyse]]})
