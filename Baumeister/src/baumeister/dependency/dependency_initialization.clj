;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.dependency.dependency-initialization
  (:use [clojure.java.io :only [as-file as-url copy]]
        [org.soulspace.clj file]
        [org.soulspace.clj.artifact artifact]
        [baumeister.config registry parameter-registry] ; TODO remove parammeter-registry 
        [baumeister.repository repositories]
        [baumeister.dependency dependency dependency-initialization]
        [baumeister.utils ant-utils log]))

; TODO instead of copying to target lib dirs, build target specific classpaths with references into the repositories?!?
(defn init-dependency [dependency]
  "Initialize the dependency for the build by copying or unzipping the referenced artifact."
  (let [artifact (:artifact dependency)
        src (query-artifact (param :deps-repositories) artifact)
        tgt (param (keyword (str "lib-" (name (:target dependency)) "-dir")))]
    (log :debug "Copying" src " -> " tgt)
    (if (nil? src)
      (log :error (artifact-name artifact) "not found in repositories!")
      (cond
        (copy? dependency) (copy src (as-file (str tgt "/" (artifact-name artifact))))
        (unzip? dependency) (ant-unzip {:src src :dest tgt :overwrite "true"})
        (follow? dependency) nil ; do nothing in initalization 
        :default (log :error "Could not handle dependency " dependency)))))

(defn init-dependencies [dependencies]
  "Initialize the sequence of dependencies."
  (doseq [dependency dependencies]
    (init-dependency dependency)))

(defn dependencies-by-targets
  "Filters the dependencies by the given targets"
  [targets dependencies]
  (let [target-set (into #{} targets)]
    (filter #(contains? target-set (:target %)) dependencies)))

(defn file-for-artifact
  ([artifact]
    (log :trace "file for artifact" (print-artifact artifact))
    (query-artifact artifact)))

(defn url-for-file
  [file]
  (log  :trace "url for file" (str file))
  (as-url (canonical-file file)))

(defn artifact-urls
  ([dependencies]
    (log :trace dependencies)
    (map url-for-file (filter (complement nil?) (map file-for-artifact (map :artifact dependencies))))))

(defn runtime-dependencies [dependencies]
  (dependencies-by-targets [:runtime :aspect] dependencies))

(defn compile-dependencies [dependencies]
  (dependencies-by-targets [:runtime :dev :aspect :aspectin] dependencies))

(defn aspect-dependencies [dependencies]
  (dependencies-by-targets [:aspect] dependencies))

(defn aspectin-dependencies [dependencies]
  (dependencies-by-targets [:aspectin] dependencies))

(defn model-dependencies [dependencies]
  (dependencies-by-targets [:model] dependencies))

(defn generator-dependencies [dependencies]
  (dependencies-by-targets [:generator] dependencies))

; TODO merge dependency-initialization with repositories query-* to dependency-resolving? 

(defn resolve-dependency 
  "Returns a resolved dependency structure (map/vec?), that contains the dependency data and the resolved artifact file."
  [dependency]
  ; what do we need? Dependency data (target), Artifact data (all?), file in local repository
  ; TODO implement with query artifact
  )