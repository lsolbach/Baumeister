(ns org.soulspace.build.baumeister.dependency.dependency-processing
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.build.baumeister.dependency dependency dependency-initialization]
        [org.soulspace.build.baumeister.utils log ant-utils]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository artifact repositories]))

(defn processed? [dep included ex]
  (let [a (:artifact dep)]
    (or (contains? included a) (contains? ex a))))

(defn add-module [module-map dep]
  (let [a (:artifact dep)]
    (assoc module-map (artifact-module-key a) a)))

(defn version-conflict? [module-map dep]
  (let [a (:artifact dep)
        pa (get module-map (artifact-module-key a))]
    (and pa (not (= (get module-map (:version (artifact-module-key a))) (:version a))))))

(defn version-conflict [module-map dep]
  (let [a (:artifact dep)
        pa (get module-map (artifact-module-key a))]
    (log :warn "version conflict for" (artifact-module-key pa) (:version pa) (:version a))))


; TODO unneccessary postprocessing of the dependency tree, handle checks on tree building
; TODO remove this method, replace with tree traversal on included dependencies of the dependency tree without enqueueing
;
; builds sequences of included and excluded dependencies/artifacts in tree order
; finds and handles version conflicts (somehow)
; used for debug output in deps.dependencies()
; the dependencies of the current node are enqueued to the queue for the preservation of the tree order
; mm is used as module map to store visited dependencies?!
; included set
; excluded set
(defn process-dependency-tree [queue module-map included excluded]
  "process the nodes of the dependency tree"
  (if-not (seq queue)
    [queue module-map included excluded] ; queue is empty, return
    (let [dep (first queue)]
      (if (processed? dep included excluded) ; check if dep was processed earlier
        (recur (rest queue) module-map included excluded) ; dep was processed, recur on rest of the queue
        (if (contains? excluded dep) ; check if dep was excluded earlier
          (recur (rest queue) module-map included excluded) ; dep was excluded earlier, recur on rest of the queue
          (if false ; (is-excluded? dep) ; check if dep is excluded
            (recur (rest queue) module-map included (conj excluded (:artifact dep))) ; dep is excluded here, add to excluded and recur on rest of the queue
            (do
              ; TODO use version matching from artifact
              (when (version-conflict? module-map dep) ; check if there's a version conflict on dep
                (version-conflict module-map dep))
              (if (seq (:dependencies dep))
                (recur (concat (rest queue) (:dependencies dep)) (add-module  module-map dep) (conj included (:artifact dep)) excluded)
                (recur (rest queue) (add-module  module-map dep) (conj included (:artifact dep)) excluded)))))))))
