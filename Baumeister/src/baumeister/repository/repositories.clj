;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.repository.repositories
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search net]
        [org.soulspace.clj.artifact artifact]
        [baumeister.config registry]
        [baumeister.repository protocol file httpproxy mavenproxy]
        [baumeister.utils log])
  (:import [baumeister.repository.file FileArtifactRepositoryImpl]
           [baumeister.repository.httpproxy HttpProxyArtifactRepositoryImpl]
           [baumeister.repository.mavenproxy MavenProxyArtifactRepositoryImpl]))

(defn maven-scope-to-target [maven-scope]
  "Maps a maven scope to a target"
  ((param :maven-scope-to-target) maven-scope))

(defn maven-type-to-type [maven-type]
  "Maps the maven type to a type."
  ((param :maven-type-to-type) maven-type))

; transforms dependency data from different repositories to baumeister dependency data
; TODO return a list of dependencies here instead of a list of dependency data?
(defmulti map-dependency
  (fn [repo dependency-data]
    (log :debug (class repo))
    (class repo)))

; transformation of maven scopes and types
(defmethod map-dependency MavenProxyArtifactRepositoryImpl
  [repo dependency-data]
  (log :debug "maven dependency data" dependency-data)
  (let [[artifact-data scope optional exclusions] dependency-data
        [project module version name type] artifact-data]
    [[project module version name (maven-type-to-type type)] (maven-scope-to-target scope) optional exclusions]))

; baumeister dependency data, no transformation needed
(defmethod map-dependency :default
  [repo dependency-data]
  (log :debug "baumeister dependency data" dependency-data)
  dependency-data)

;
; TODO think about the repository design
;
(defn get-dev-repository [repositories]
  (first (filter #(= (:usage %) :development) repositories)))

(defn get-staging-repository [repositories]
  (first (filter #(= (:usage %) :staging) repositories)))

(defn get-release-repository [repositories]
  (first (filter #(= (:usage %) :release) repositories)))

(defn repositories-by-usage [repositories usage]
  (filter #(= (:usage %) (keyword usage)) repositories))

;
; repository query functions
;
; TODO merge dependency-initialization with repositories query-* to dependency-resolving?
(defn query-artifact
  "Query the configured repositories for an artifact."
  ([artifact]
   (log :debug "Querying artifact:" (artifact-name-version artifact))
   (query-artifact (param :deps-repositories) artifact))
  ([repositories artifact]
   (if (seq repositories)
     ; iterate through the configured repositories to find this artifact
     (let [repo (first repositories)
           artifact-file (find-artifact repo artifact)]
       (log :trace "querying repository" repo)
       (if (exists? artifact-file)
         artifact-file ; found, return artifact file
         (recur (rest repositories) artifact))) ; not found, try next repository
     (do
       (log :warn "Could not find artifact" (artifact-name-version artifact) "in the configured repositories!")
       nil))))

(defn query-dependencies
  "Query the configured repositories for transitive dependencies."
  ([artifact]
   (log :debug "Querying transitive dependencies for artifact:" (artifact-key artifact))
   (query-dependencies (param :deps-repositories) artifact))
  ([repositories artifact]
   (if (seq repositories)
     ; Iterate through the configured repositories to find the dependencies file for this artifact
     (let [repo (first repositories)
           deps (get-dependencies-for-artifact repo artifact)]
       (log :trace "Querying depependencies from repository:" repo)
       ; test for nil instead of seq to distinguish between no module/pom file or no dependencies in module/pom file
       (if (nil? deps)
         (recur (rest repositories) artifact) ; not found, try next repository
         (map (partial map-dependency repo) deps))) ; found, return dependency
     (do
       (log :warn "Could not find dependencies for" (artifact-name-version artifact) "in the configured repositories!")
       nil))))
