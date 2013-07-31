(ns org.soulspace.build.baumeister.process
  (:use [clojure.tools.cli :only [cli]]
        [org.soulspace.clj string file]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister workflow-engine]
        [org.soulspace.build.baumeister.utils log cli classpath])
  (:import [java.util Date])
  (:gen-class))

(def option-spec
  [["-D" "--define" "Define a parameter (format -Dname=value)"]
   ["-f" "--file" "Use the specified file instead of the default module.clj (format -Dfilename)"]
   ["-h" "--help" "Display help (format -h)"]
   ["-v" "--version" "Display version"]])

(defn start-workflow
  "Start the Baumeister workflow."
  ([]
    (do-workflow :build-workflow)) ; default workflow is :build-workflow
  ([& args]
    (doseq [workflow (seq args)]
      (do-workflow (keyword workflow)))))

; TODO check to (set! *read-eval* false) to prevent security issues reading files
; TODO handle args
(defn -main [& args]
  "Baumeister main method."
  (let [start (System/currentTimeMillis)
        [options arguments] (parse-args args)]
    (log :info "Started at" (Date. start))
    (init-config)
    (apply start-workflow arguments)
    (let [end (System/currentTimeMillis)] (log :info "Done at" (Date. end) ", duration" (/ (- end start) 1000.0) "seconds.")))
  "Done!")
