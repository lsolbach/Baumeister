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
  )


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
    (query-artifact (param :deps-repositories) artifact))
  ([repositories artifact]
    (log :debug "Querying artifact:" (artifact-name-version artifact))
    (if (seq repositories)
      ; Iterate through the configured repositories to find this artifact
      (let [repo (first repositories)
            artifact-file (find-artifact repo artifact)]
        (log :trace "querying repository" repo)
        (if (exists? artifact-file)
          artifact-file ; return artifact file
          (recur (rest repositories) artifact))) ; try next repository
      (do 
        (log :warn "Could not find artifact" (artifact-name-version artifact) "in the configured repositories!")
        nil))))

(defn query-dependencies
  "Query the configured repositories for transitive dependencies."
  ([artifact]
    (query-dependencies (param :deps-repositories) artifact))
  ([repositories dependency]
    (log :debug "Querying transitive dependencies for dependency:" (artifact-key (:artifact dependency)) (:target dependency))
    (if (seq repositories)
      ; Iterate through the configured repositories to find the dependencies file for this artifact
      (let [repo (first repositories)
            deps (get-dependencies-for-artifact repo (:artifact dependency))]
        (log :trace "Querying depependencies from repository:" repo)
        ; test for nil instead of seq to distinguish between no module/pom file or no dependencies in module/pom file
        (if (nil? deps)
          (recur (rest repositories) dependency) ; try next repository
          deps)) ; return dependency data
      nil)))
