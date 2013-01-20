(ns org.soulspace.build.baumeister.process
  (:use [org.soulspace.clj.lib.file]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister workflow-engine]
        [org.soulspace.build.baumeister.utils log])
  (:gen-class))

; TODO check to (set! *read-eval* false) to prevent security issues reading files
(defn -main [& args]
  "Baumeister entry method."
  (log :debug "Current dir: " (System/getProperty "user.dir"))
  (init-config)
  (if (seq args)
    (doseq [wf args]
      (do-workflow (keyword wf)))
    (do-workflow :build-workflow)))
