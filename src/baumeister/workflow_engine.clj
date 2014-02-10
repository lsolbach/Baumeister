;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.workflow-engine
  (:use [org.soulspace.clj string]
        [baumeister.utils checks log message]
        [baumeister.config registry function-registry]))

(defn workflow? [id] (ends-with "-workflow" (name id)))
(defn phase? [id] (not (ends-with "-workflow" (name id))))

(defn pre-key [phase] (keyword (str "pre-" (name phase))))
(defn post-key [phase] (keyword (str "post-" (name phase))))

(defn do-step [step functions]
  (when (seq functions)
    (message :important (str "doing step " (str (name step) "... " functions)))
    (doseq [function functions]
      (function))))

(defn do-phase
  "process the registered functions for the phase"
  [phase]
  (message :important  (str "doing phase " (name phase) "... "))
  (do-step (pre-key phase) (get-registered-step-functions (pre-key phase)))
  (do-step phase (get-registered-step-functions phase))
  (do-step (post-key phase) (reverse (get-registered-step-functions (post-key phase)))))

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
