(ns org.soulspace.build.baumeister.process
  (:use [org.soulspace.clj file]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister workflow-engine]
        [org.soulspace.build.baumeister.utils log])
  (:import [java.util Date])
  (:gen-class))

(defn start-workflow
  "Start the Baumeister "
  ([]
    (do-workflow :build-workflow))
  ([args]
    (doseq [wf args]
      (do-workflow (keyword wf)))))

; TODO check to (set! *read-eval* false) to prevent security issues reading files
(defn -main [& args]
  "Baumeister entry method."
  (let [start (System/currentTimeMillis)]
    (log :info "Started at" (Date. start))
    (init-config)
    (apply start-workflow args)
    (let [end (System/currentTimeMillis)] (log :info "Done at" (Date. end) ", duration" (/ (- end start) 1000.0) "seconds.")))
  "Done!")
