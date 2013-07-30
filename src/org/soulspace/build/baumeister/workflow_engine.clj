(ns org.soulspace.build.baumeister.workflow-engine
  (:use [org.soulspace.clj string]
        [org.soulspace.build.baumeister.utils checks log]
        [org.soulspace.build.baumeister.config registry function-registry]))

(defn workflow? [id] (ends-with "-workflow" (name id)))
(defn phase? [id] (not (ends-with "-workflow" (name id))))

(defn pre-key [phase]
  (keyword (str "pre-" (name phase))))

(defn post-key [phase]
  (keyword (str "post-" (name phase))))

(defn log-fns [steps]
  (doseq [step steps]
    (log :warn (:doc step))))

(defn do-steps [steps]
  (doseq [step steps]
    (step)))

(defn do-phase [phase]
  (log :info  (str "doing phase " (name phase) "... "))
  (log :debug  (str "doing step " (str "pre-" (name phase)) "... " (get-registered-functions (pre-key phase))))
  (do-steps (get-registered-functions (pre-key phase)))
  (log :debug  (str "doing step " (name phase) "... " (get-registered-functions phase)))
  (do-steps (get-registered-functions phase))
  (log :debug  (str "doing step " (str "post-" (name phase)) "... " (get-registered-functions (post-key phase))))
  (do-steps (reverse (get-registered-functions (post-key phase)))))

(defn do-workflow
  "process the given workflow (or phase)"
  [workflow]
  (if (workflow? workflow)
    (do (log :info  "starting workflow" workflow)
      (doseq [phase ((param :workflow-definitions) workflow)]
        (if (workflow? phase)
          (do-workflow phase)
          (do-phase phase))))
    (do-phase workflow)))
