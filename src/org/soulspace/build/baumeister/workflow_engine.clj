(ns org.soulspace.build.baumeister.workflow-engine
  (:use [org.soulspace.build.baumeister.utils checks log]
        [org.soulspace.build.baumeister.config registry]))

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
  (log :debug  (str "doing step " (str "pre-" (name phase)) "... " ((pre-key phase) fn-registry)))
  (do-steps ((pre-key phase) fn-registry))
  (log :debug  (str "doing step " (name phase) "... " (phase fn-registry)))
  (do-steps (phase fn-registry))
  (log :debug  (str "doing step " (str "post-" (name phase)) "... " ((post-key phase) fn-registry)))
  (do-steps (reverse ((post-key phase) fn-registry))))

(defn do-workflow
  "process the given workflow (or phase)"
  [workflow]
  (if (workflow? workflow)
    (do (log :info  "starting workflow" workflow)
      (doseq [phase ((workflow-phases) workflow)]
        (if (workflow? phase)
          (do-workflow phase)
          (do-phase phase))))
    (do-phase workflow)))
