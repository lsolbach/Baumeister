;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.dependency.dependency-initialization
  (:use [clojure.java.io :only [as-file as-url copy]]
        [org.soulspace.clj file]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories]
        [org.soulspace.build.baumeister.dependency dependency dependency-initialization]
        [org.soulspace.build.baumeister.utils ant-utils checks log]))

(defn init-dependency [dependency]
  "Initialize the dependency for the build by copying or unzipping the referenced artifact."
  (let [artifact (:artifact dependency)
        src (query-artifact (param :deps-repositories) artifact)
        tgt (param (keyword (str "lib-" (name (:target dependency)) "-dir")))]
    (log :debug "Copying" src " -> " tgt)
    (if (nil? src)
      (do
        (log :error (artifact-name artifact) "not found in repositories!"))
      ; (throw (RuntimeException. (str "Error: " (artifact-name artifact) " not found!")))
      (cond
        (copy? dependency)
        (copy src (as-file (str tgt "/" (artifact-name artifact))))
        (unzip? dependency)
        (ant-unzip {:src src :dest tgt :overwrite "true"})
        (follow? dependency)
        nil ; do nothing in initalization 
        :default
        (log :error "Could not handle dependency " dependency)))))

(defn init-dependencies [dependencies]
  "Initialize the sequence of dependencies."
  (doseq [dependency dependencies]
    (init-dependency dependency)))

(defn dependency-urls
  [dependencies]
  (let [urls
        (->>
          (map :artifact dependencies)
          (map (partial query-artifact (param :deps-repositories)))
          (map canonical-file)
          (map as-url))]
    urls))
  