(ns org.soulspace.build.baumeister.process
  (:use [clojure.tools.cli :only [cli]]
        [clojure.repl]
        [org.soulspace.clj string file]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister workflow-engine]
        [org.soulspace.build.baumeister.utils classpath cli log message classpath])
  (:import [java.util Date])
  (:gen-class))

; TODO check to (set! *read-eval* false) to prevent security issues reading files
(set! *read-eval* false)

(def option-defs
  "Baumeister option definitions."
  [["--define" "-D" "Define a parameter (format -Dname=value)" :multi true]
   ["--file" "-f" "Use the specified module file (format -Dfilename)" :default "module.clj"]
   ["--help" "-h" "Display help (format -h)"]
   ["--version" "-v" "Display version"]])

(defn -main
  "Baumeister main method."
  [& args]
  (let [start (System/currentTimeMillis)
        [arguments options] (parse-args args option-defs)]
    (if (and (nil? (:help options)) (nil? (:version options)))
      (do ; Workflow
        (init-config options)
        (message :normal "Started at" (Date. start))
        (apply start-workflow arguments)
        (let [end (System/currentTimeMillis)] (message :important (str "Done at " (Date. end) ", duration " (/ (- end start) 1000.0) " seconds."))))
      (do ; Version/Help options
        (if-not (nil? (:version options))
          (println (param :system-version)))
        (when-not (nil? (:help options))
          (println "Baumeister usage:")
          (println (doc-options option-defs))))))
  0)
