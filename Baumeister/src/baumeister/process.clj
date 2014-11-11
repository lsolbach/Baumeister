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

(def option-defs
  "Baumeister option definitions."
  [["--define" "-D" "Define a parameter (eg. -Dname=value)" :multi true]
   ["--file" "-f" "Use the specified module file (eg. -Dfilename)" :default "module.clj"]
   ["--help" "-h" "Display help (format -h)"]
   ["--version" "-v" "Display version"]
   ])

(defn -main
  "Baumeister main method."
  [& args]
  (let [start (System/currentTimeMillis)
        [arguments options] (parse-args args option-defs)]
    (if (and (nil? (:help options)) (nil? (:version options)))
      (do ; Workflow
        (message :info "Started at" (Date. start))
        (init-config options)
        (apply start-workflow arguments)
        (let [end (System/currentTimeMillis)]
          (message :info (str "Done at " (Date. end) ", duration " (/ (- end start) 1000.0) " seconds."))))
      (do ; Version/Help options
        (init-defaults) ; initialize defaults only
        (if-not (nil? (:version options))
          (println "Baumeister version: " (param :system-version)))
        (when-not (nil? (:help options))
          (println "Baumeister usage:")
          (println (doc-options option-defs))))))
  0)
