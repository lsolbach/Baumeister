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
  (:require [clojure.java.io :as io]
            [org.soulspace.clj.file :as file]
            [org.soulspace.tools.artifact :as artifact]
            [baumeister.utils.ant-utils :as ant]
            [baumeister.utils.log :as log]
            [baumeister.config.registry :as reg]
            [baumeister.repository.repositories :as repo]
            [baumeister.dependency.dependency :as dep]))

; TODO instead of copying to target lib dirs, build target specific classpaths with references into the repositories?!?
(defn init-dependency
  "Initializes the dependency for the build by copying or unzipping the referenced artifact."
  [dependency]
  (let [artifact (:artifact dependency)
        src (repo/query-artifact (reg/param :deps-repositories) artifact)
        tgt (reg/param (keyword (str "lib-" (name (:target dependency)) "-dir")))]
    (if (nil? src)
      (log/log :error (artifact/artifact-name artifact) "not found in repositories!")
      (cond
        (dep/copy? dependency) (do
                             (log/log :debug "Copying" (dep/print-dependency dependency) "->" tgt)
                             (io/copy src (io/as-file (str tgt "/" (artifact/artifact-name artifact)))))
        (dep/unzip? dependency) (do
                              (log/log :debug "Unpacking" (dep/print-dependency dependency) "->" tgt)
          (ant/ant-unzip {:src src :dest tgt :overwrite "true"}))
        (dep/follow? dependency) (do
                               (log/log :debug "Following" (dep/print-dependency dependency))
          nil) ; do nothing in initalization 
        :default (log/log :error "Could not handle dependency " (dep/print-dependency dependency))))))

(defn init-dependencies
  "Initializes the sequence of dependencies."
  [dependencies]
  (doseq [dependency dependencies]
    (init-dependency dependency)))

(defn dependencies-by-targets
  "Filters the dependencies by the given targets"
  [targets dependencies]
  (let [target-set (into #{} targets)]
    (filter #(contains? target-set (:target %)) dependencies)))

(defn file-for-artifact
  "Returns the file for the artifact."
  ([artifact]
    (log/log :trace "file for artifact" (dep/print-artifact artifact))
    (repo/query-artifact artifact)))

(defn url-for-file
  "Returns the url for the given file."
  [file]
  (log/log  :trace "url for file" (str file))
  (io/as-url (file/canonical-file file)))

(defn artifact-urls
  "Returns the artifact urls for the dependencies."
  ([dependencies]
    (log/log :trace dependencies)
    (map url-for-file (filter (complement nil?) (map file-for-artifact (map :artifact dependencies))))))

; TODO make the targets configurable in the settings
(defn runtime-dependencies
  "Returns the runtime dependencies."
  [dependencies]
  (dependencies-by-targets [:runtime :aspect] dependencies))

(defn compile-dependencies
  "Returns the compile time dependencies."
  [dependencies]
  (dependencies-by-targets [:runtime :dev :aspect :aspectin] dependencies))

(defn aspect-dependencies
  "Returns the aspect dependencies."
  [dependencies]
  (dependencies-by-targets [:aspect] dependencies))

(defn aspectin-dependencies
  "Returns the aspectin dependencies."
  [dependencies]
  (dependencies-by-targets [:aspectin] dependencies))

(defn model-dependencies
  "Returns the model dependencies."
  [dependencies]
  (dependencies-by-targets [:model] dependencies))

(defn generator-dependencies
  "Returns the generator dependencies."
  [dependencies]
  (dependencies-by-targets [:generator] dependencies))

; TODO merge dependency-initialization with repositories query-* to dependency-resolving? 

(defn resolve-dependency 
  "Returns a resolved dependency structure (map/vec?), that contains the dependency data and the resolved artifact file."
  [dependency]
  ; what do we need? Dependency data (target), Artifact data (all?), file in local repository
  ; TODO implement with query artifact
  )
