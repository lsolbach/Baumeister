(ns org.soulspace.build.baumeister.dependency.dependency-processing
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.dependency dependency]
        [org.soulspace.build.baumeister.utils log ant-utils]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories]))

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

(defn process-artifact [artifact]
  (let [src (query-artifact (param :deps-repositories) artifact)
        tgt (param (keyword (str "lib-" (:target artifact) "-dir")))]
    (if (nil? src)
      (log :error (artifact-path artifact) "not found in repositories!")
      ; (throw (RuntimeException. (str "Error: " (artifact-path artifact) " not found!")))
      (cond
        (copy? artifact)
        (copy src (as-file (str tgt "/" (:artifact artifact) "." (:type artifact))))
        (unzip? artifact)
        (ant-unzip {:src src :dest tgt :overwrite "true"})))))

(defn process-artifacts [coll]
  (loop [artifacts coll
         missing []]
    (if (seq artifacts)
      (do
        (let [miss (process-artifact (first artifacts))]
          (if (nil? miss)
            (recur (rest artifacts) missing)
            (recur (rest artifacts) (conj missing miss)))))
      (when (seq missing)
        (throw (RuntimeException. (str "Error: " (map artifact-path missing) "could not be found!")))))))
      ;    (doseq [artifact coll]
      ;      (process-artifact artifact)))

; TODO old way, remove when transitive dependencies work
(defn process-dependencies [dependencies]
  "process dependencies as specified in the current module (not transitive)"
  (doseq [dependency dependencies]
    (process-artifact (apply new-artifact dependency))))

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
          (if (is-excluded? dep) ; check if dep is excluded
            (recur (rest queue) module-map included (conj excluded (:artifact dep))) ; dep is excluded here, add to excluded and recur on rest of the queue
            (do
              ; TODO use version matching from artifact
              (when (version-conflict? module-map dep) ; check if there's a version conflict on dep
                (version-conflict module-map dep))
              (if (seq (:dependencies dep))
                (recur (concat (rest queue) (:dependencies dep)) (add-module  module-map dep) (conj included (:artifact dep)) excluded)
                (recur (rest queue) (add-module  module-map dep) (conj included (:artifact dep)) excluded)))))))))

