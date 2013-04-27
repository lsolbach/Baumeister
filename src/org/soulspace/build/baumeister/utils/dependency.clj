(ns org.soulspace.build.baumeister.utils.dependency
  (:use [org.soulspace.clj file]
        [org.soulspace.build.baumeister.config registry]))

(def dependency-config
  (load-file (str (get-home) "/config/dependency_defaults.clj")))

(defn copy? [dependency]
  (contains? (:copy (:actions dependency-config)) (:target dependency)))

(defn unzip? [dependency]
  (contains? (:unzip (:actions dependency-config)) (:target dependency)))

(defn follow? [dependency]
  (contains? (:follow (:actions dependency-config)) (:target dependency)))

(defn exclude? [dependency]
  (contains? (:exclude (:actions dependency-config)) (:target dependency)))

(defprotocol Dependency
  (is-excluded? [node] "returns true if the artifact of this dependency is excluded")
  (includes [node])
  (excludes [node]))

; TODO rethink whats part of the artifact and whats part of the dependency
; Part of the dependency: artifact target scope dependencies exclusions
(defrecord DependencyImpl [artifact target scope dependencies exclusions]
  Dependency
  (is-excluded? [node]
                (exclude? (:artifact node)))
  (includes [node]
            (filter #(not (exclude? %)) dependencies))
  (excludes [node]
            (filter #(exclude? %) dependencies)))

; TODO add required signatures
(defn new-dependency
  ([artifact]
    (DependencyImpl. artifact "runtime" nil nil nil))
  ([artifact target]
    (DependencyImpl. artifact target nil nil nil))
  ([artifact target dependencies]
    (DependencyImpl. artifact target nil dependencies nil))
  ([artifact target dependencies exclusions]
    (DependencyImpl. artifact target nil dependencies exclusions))
  ([artifact target dependencies exclusions scope]
    (DependencyImpl. artifact target scope dependencies exclusions)))

(defn create-dependency-dispatch
  [x & xs]
  (cond
    (map? x) :artifact-map
    (sequential? x) :artifact-seq
    :default :artifact-flat))

(defmulti create-dependency
  "create a new dependency"
  #'create-dependency-dispatch)

(defmethod create-dependency :artifact-map
  [x & xs]
  (println ":artifact-map" x xs)
  )

(defmethod create-dependency :artifact-seq
  [x & xs]
  (println ":artifact-seq" x xs)
  )

(defmethod create-dependency :artifact-flat
  [x & xs]
  (println ":artifact-flat" x xs)
  )
