;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.process
  (:require [org.soulspace.clj.cli :as cli]
            [org.soulspace.clj.java.system :as sys]
            [baumeister.config.registry :as reg]
            [baumeister.utils.log :as log]
            [baumeister.workflow-engine :as engine])
  (:import [java.util Date])
  (:gen-class))

(def known-commands #{:new :run})

(def option-defs
  "Baumeister option definitions."
  [["--help" "-h" "Display help"]
   ["--version" "-v" "Display the Baumeister version information"]
   ["--file" "-f" "Use the specified module file (eg. -Dfilename)" :default "module.clj"]
   ["--define" "-D" "Define a parameter (eg. -Dname=value)" :multi true]
   ;["--new" "-n" "Create a new module (format -n <name> [<template>])"] ; implement
   ;["--run" "-r" "Run an application (format -r [<command> [<options>]])"] ; implement
   ["--print-config" "-c" "Print the effective configuration after initialization"]])

(defn print-only-options?
  "Checks if print-only options are given."
  [options]
  (let [options (into #{} (keys options))]
    (or (contains? options :help)
        (contains? options :version))))

(defn command?
  "Checks if a command is given."
  [argument]
  (contains? known-commands (keyword argument)))

(defn new
  "Creates a new module."
  [arguments options]
  (let [new-config [:plugins ["org.soulspace.baumeister/GenesisPlugin"]
                    :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                                   ["org.soulspace.baumeister/AspectJTemplate, 0.1.0, AspectJTemplate, zip" :data]
                                   ["org.soulspace.baumeister/BaumeisterPluginTemplate, 0.1.0, BaumeisterPluginTemplate, zip" :data]
                                   ["org.soulspace.baumeister/BaumeisterTemplateTemplate, 0.1.0, BaumeisterTemplateTemplate, zip" :data]
                                   ["org.soulspace.baumeister/ClojureTemplate, 0.1.0, ClojureTemplate, zip" :data]
                                   ["org.soulspace.baumeister/DataTemplate, 0.1.0, DataTemplate, zip" :data]
                                   ["org.soulspace.baumeister/JavaTemplate, 0.1.0, JavaTemplate, zip" :data]]]]
    ; TODO (configure-from-seq options)
    (try
      (reg/init-config new-config options)
      (apply engine/start-workflow "new-workflow")
      (catch Exception e
        (log/message :error (.getMessage e))
        (log/message :debug (.printStackTrace e))))))

(defn run
  "Runs an application."
  [arguments options]
  ; TODO implement application start 
  )

(defn print-options
  "Print some messages and quit."
  [options]
  (reg/init-defaults) ; initialize defaults only
  (if-not (nil? (:version options))
    (println "Baumeister version: " (reg/param :system-version)))
  (when-not (nil? (:help options))
    (println "Baumeister usage:")
    (println (cli/doc-options option-defs))))

(defn start-processing
  "Start processing."
  [arguments options]
  (let [start (System/currentTimeMillis)]
    (log/message :info "Started at" (Date. start))
    (if (command? (first arguments))
      (try 
        (println (first arguments) (rest arguments) options)
        ((symbol (first arguments)) (rest arguments) options)
        (catch Exception e
          (log/message :error (.getMessage e))
          (log/message :debug (.printStackTrace e))))
      (try 
        (reg/init-config options)
        (apply engine/start-workflow arguments)
        (catch Exception e
          (log/message :error (.getMessage e))
          (log/message :debug (.printStackTrace e)))))
    (let [end (System/currentTimeMillis)]
      (log/message :info (str "Done at " (Date. end) ", duration " (/ (- end start) 1000.0) " seconds.")))))

(defn test-main
  "Baumeister test method."
  [& args]
  (reg/init-defaults [:log-level :trace
                  :baumeister-home-dir "."
                  :user-home-dir "."
                  :java-home (sys/get-environment-variable "JAVA_HOME")
                  :aspectj-home (sys/get-environment-variable "ASPECTJ_HOME")
                  :repository-home-dir (sys/get-environment-variable "BAUMEISTER_REPOS" "/home/soulman/devel/repositories")])
  (let [[arguments options] (cli/parse-args args option-defs)]
    (if (print-only-options? options)
      (print-options options)
      (start-processing arguments options)))
  0)

(defn -main
  "Baumeister main method."
  [& args]
  (reg/init-defaults)
  (let [[arguments options] (cli/parse-args args option-defs)]
    ;(println "Options:" options)
    (if (print-only-options? options)
      (print-options options)
      (start-processing arguments options)))
  0)
