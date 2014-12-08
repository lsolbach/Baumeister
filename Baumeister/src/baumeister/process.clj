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
        [org.soulspace.clj.application cli classpath] 
        [baumeister.config config]
        [baumeister.config registry]
        [baumeister workflow-engine]
        [baumeister.utils log])
  (:import [java.util Date])
  (:gen-class))

(def commands #{:new})

(def option-defs
  "Baumeister option definitions."
  [["--help" "-h" "Display help"]
   ["--version" "-v" "Display the Baumeister version information"]
   ["--file" "-f" "Use the specified module file (eg. -Dfilename)" :default "module.clj"]
   ["--define" "-D" "Define a parameter (eg. -Dname=value)" :multi true]
   ["--new" "-n" "New project (format -n <name> <template>)"]
   ["--print-config" "-c" "Print the effective configuration after initialization"]
   ])

(defn print-only-options?
  "Checks if print-only options are given."
  [options]
  (let [options (into #{} (keys options))]
    (or (contains? options :help)
        (contains? options :version))))

(defn command?
  "Checks if a command is given."
  [argument]
  (contains? #{:new} (keyword argument)))

(defn print-options
  "Print some messages and quit."
  [options]
  (init-defaults) ; initialize defaults only
  (if-not (nil? (:version options))
    (println "Baumeister version: " (param :system-version)))
  (when-not (nil? (:help options))
    (println "Baumeister usage:")
    (println (doc-options option-defs))))

(defn process-command
  "Process command (aka module-less workflow)"
  [command arguments options]
  )

(defn start-processing
  "Start processing."
  [arguments options]
  (let [start (System/currentTimeMillis)]
    (message :info "Started at" (Date. start))
    (init-config options)
    (if (command? (first arguments))
      (process-command (first arguments) (rest arguments) options)
      (apply start-workflow arguments))
    (let [end (System/currentTimeMillis)]
      (message :info (str "Done at " (Date. end) ", duration " (/ (- end start) 1000.0) " seconds.")))))

(defn -main
  "Baumeister main method."
  [& args]
  (let [[arguments options] (parse-args args option-defs)]
    (println "Options:" options)
    (if (print-only-options? options)
      (print-options options)
      (start-processing arguments options)))
  0)
