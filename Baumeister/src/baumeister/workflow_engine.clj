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
  (:require [clojure.string :as str :only [ends-with?]])
  (:use [org.soulspace.clj string]
        [baumeister.utils checks log]
        [baumeister.config registry function-registry]))

(defn workflow?
  "Returns true if the given id identifies a workflow."
  [id]
  (str/ends-with? (name id) "-workflow"))

(defn phase?
  "Returns true if the given id identifies a workflow phase."
  [id]
  (not (str/ends-with? (name id) "-workflow")))

(defn pre-key
  "Returns the key of the pre step of the phase."
  [phase]
  (keyword (str "pre-" (name phase))))

(defn post-key
  "Returns the key of the post step of the phase."
  [phase]
  (keyword (str "post-" (name phase))))

(defn do-step
  "Processes the functions for the step."
  [step functions]
  (when (seq functions)
    (message :trace (str "doing step " (str (name step) "... ")))
    (log :trace (str "step " (str (name step) functions)))
    (doseq [function functions]
      (function))))

(defn do-phase
  "Processes the registered functions for the phase."
  [phase]
  (message :info  (str "doing phase " (name phase) "... "))
  (do-step (pre-key phase) (get-registered-step-functions (pre-key phase)))
  (do-step phase (get-registered-step-functions phase))
  (do-step (post-key phase) (reverse (get-registered-step-functions (post-key phase)))))

(defn do-workflow
  "Processes the given workflow (or phase)"
  [workflow]
  (if (workflow? workflow)
    (do
      (message :important  "doing workflow" workflow)
      (log :debug "workflow phases" ((param :workflow-definitions) workflow))
      (doseq [phase ((param :workflow-definitions) workflow)]
        (if (workflow? phase)
          (do-workflow phase)
          (do-phase phase))))
    (do-phase workflow)))

(defn start-workflow
  "Starts the workflow."
  ([]
   (do-workflow (keyword (param :default-action)))) ; default workflow is :build-workflow
  ([& args]
   (doseq [workflow (seq args)]
     (do-workflow (keyword workflow)))))
