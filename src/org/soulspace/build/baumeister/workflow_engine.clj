(ns org.soulspace.build.baumeister.workflow-engine
  (:use [org.soulspace.clj string]
        [org.soulspace.build.baumeister.utils checks log message]
        [org.soulspace.build.baumeister.config registry function-registry]))

(defn workflow? [id] (ends-with "-workflow" (name id)))
(defn phase? [id] (not (ends-with "-workflow" (name id))))

(defn pre-key [phase]
  (keyword (str "pre-" (name phase))))

(defn post-key [phase]
  (keyword (str "post-" (name phase))))

(defn do-functions [step functions]
  (when (seq functions)
    (message :important (str "doing step " (str (name step) "... " functions)))
    (doseq [function functions]
      (function))))

(defn do-phase [phase]
  (message :important  (str "doing phase " (name phase) "... "))
  (do-functions (pre-key phase) (get-registered-functions (pre-key phase)))
  (do-functions phase (get-registered-functions phase))
  (do-functions (post-key phase) (reverse (get-registered-functions (post-key phase)))))

(defn do-workflow
  "process the given workflow (or phase)"
  [workflow]
  (if (workflow? workflow)
    (do (message :important  "doing workflow" workflow)
      (doseq [phase ((param :workflow-definitions) workflow)]
        (if (workflow? phase)
          (do-workflow phase)
          (do-phase phase))))
    (do-phase workflow)))

(defn start-workflow
  "Start the Baumeister workflow."
  ([]
    (do-workflow :build-workflow)) ; default workflow is :build-workflow
  ([& args]
    (doseq [workflow (seq args)]
      (do-workflow (keyword workflow)))))
