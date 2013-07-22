(ns org.soulspace.build.baumeister.process
  (:use [org.soulspace.clj file]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister workflow-engine]
        [org.soulspace.build.baumeister.utils log])
  (:import [java.util Date])
  (:gen-class))

; TODO check to (set! *read-eval* false) to prevent security issues reading files
(defn -main [& args]
  "Baumeister entry method."
  (let [start (System/currentTimeMillis)]
    (log :info "Started at" (Date. start))
    (log :debug "Current dir:" (System/getProperty "user.dir"))
    (init-config)
    (if (seq args)
      (doseq [wf args]
        (do-workflow (keyword wf)))
      (do-workflow :build-workflow))
    (let [end (System/currentTimeMillis)]
      (log :info "Done at" (Date. end) " duration" (/ (- end start) 1000.0) "seconds.")))
  "Done!")
