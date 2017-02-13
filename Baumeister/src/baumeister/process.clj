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
  (:use [org.soulspace.clj string file]
        [org.soulspace.clj.application cli classpath env-vars] 
        [baumeister.config config]
        [baumeister.config registry]
        [baumeister workflow-engine]
        [baumeister.utils log])
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
      (init-config new-config options)
      (apply start-workflow "new-workflow")
      (catch Exception e
        (message :error (.getMessage e))
        (message :debug (.printStackTrace e))))))

(defn run
  "Runs an application."
  [arguments options]
  ; TODO implement application start 
  )

(defn print-options
  "Print some messages and quit."
  [options]
  (init-defaults) ; initialize defaults only
  (if-not (nil? (:version options))
    (println "Baumeister version: " (param :system-version)))
  (when-not (nil? (:help options))
    (println "Baumeister usage:")
    (println (doc-options option-defs))))

(defn start-processing
  "Start processing."
  [arguments options]
  (let [start (System/currentTimeMillis)]
    (message :info "Started at" (Date. start))
    (if (command? (first arguments))
      (try 
        (println (first arguments) (rest arguments) options)
        ((symbol (first arguments)) (rest arguments) options)
        (catch Exception e
          (message :error (.getMessage e))
          (message :debug (.printStackTrace e))))
      (try 
        (init-config options)
        (apply start-workflow arguments)
        (catch Exception e
          (message :error (.getMessage e))
          (message :debug (.printStackTrace e)))))
    (let [end (System/currentTimeMillis)]
      (message :info (str "Done at " (Date. end) ", duration " (/ (- end start) 1000.0) " seconds.")))))

(defn test-main
  "Baumeister test method."
  [& args]
  (init-defaults [:log-level :trace
                  :baumeister-home-dir "."
                  :user-home-dir "."
                  :java-home (get-env "JAVA_HOME")
                  :aspectj-home (get-env "ASPECTJ_HOME")])
  (let [[arguments options] (parse-args args option-defs)]
    (if (print-only-options? options)
      (do 
        (print-options options)
        )
      (do
        (start-processing arguments options))
      ))
  0)


(defn -main
  "Baumeister main method."
  [& args]
  (init-defaults)
  (let [[arguments options] (parse-args args option-defs)]
    ;(println "Options:" options)
    (if (print-only-options? options)
      (print-options options)
      (start-processing arguments options)))
  0)
