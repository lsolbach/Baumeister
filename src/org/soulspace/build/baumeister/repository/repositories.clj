(ns org.soulspace.build.baumeister.repository.repositories
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function net]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository protocol file httpproxy mavenproxy]
        [org.soulspace.build.baumeister.utils log]
        [org.soulspace.build.baumeister.maven maven-utils])
  (:import [org.soulspace.build.baumeister.repository.file FileArtifactRepositoryImpl]
           [org.soulspace.build.baumeister.repository.httpproxy HttpProxyArtifactRepositoryImpl]
           [org.soulspace.build.baumeister.repository.mavenproxy MavenProxyArtifactRepositoryImpl])
  )

(defmulti create-repository first)
(defmethod create-repository :file [opts]
  (let [[_ usage path] opts]
    (FileArtifactRepositoryImpl. usage (param path))))
(defmethod create-repository :http-proxy [opts]
  (let [[_ usage url path] opts] 
    (HttpProxyArtifactRepositoryImpl. usage url (param path))))
(defmethod create-repository :maven-proxy [opts]
  (let [[_ usage url path] opts] 
    (MavenProxyArtifactRepositoryImpl. usage url (param path))))

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
(defn query-artifact 
  "Query the configured repositories for an artifact."
  ([artifact]
    (query-artifact (param :deps-repositories) artifact))
  ([repositories artifact]
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
