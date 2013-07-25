(ns org.soulspace.build.baumeister.repository.repositories
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function net]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository 
         artifact version repository-protocol file-repository http-proxy-repository maven-proxy-repository]
        [org.soulspace.build.baumeister.utils log]
        [org.soulspace.build.baumeister.maven maven-utils]))

;
; repository functions
;
(defn create-repositories [v]
  (map create-repository v))

(defn get-dev-repository [repositories]
  (first (filter #(= (:usage %) :development) repositories)))

(defn get-release-repository [repositories]
  (first (filter #(= (:usage %) :release) repositories)))

(defn repositories-by-usage [repositories usage]
  (filter #(= (:usage %) (keyword usage)) repositories))

;
; query functions
;
(defn query-artifact [repositories artifact]
  "Query the configured repositories for an artifact."
  (log :debug "Querying artifact:" (artifact-name-version artifact))
  (if (seq repositories)
    ; Iterate through the configured repositories to find this artifact
    (let [repo (first repositories)
          artifact-file (get-artifact repo artifact)]
      (log :trace "querying repository" repo)
      (if (exists? artifact-file)
        artifact-file ; return artifact file
        (recur (rest repositories) artifact))) ; try next repository
    (do (log :warn "Could not find artifact" (artifact-name-version artifact) "in the configured repositories!")
      nil)))

(defn query-dependencies [repositories dependency]
  "Query the configured repositories for transitive dependencies."
  (log :debug "Querying transitive dependencies for dependency:" dependency)
  (if (seq repositories)
    ; Iterate through the configured repositories to find the dependencies file for this artifact
    (let [repo (first repositories)
          deps (get-dependencies-for-artifact repo (:artifact dependency))]
      (log :trace "Querying depependencies from repository:" repo)
      ; test for nil instead of seq to distinguish between no module/pom file or no dependencies in module/pom file
      (if (nil? deps)
        (recur (rest repositories) dependency) ; try next repository
        deps)) ; return dependency data
    nil))
