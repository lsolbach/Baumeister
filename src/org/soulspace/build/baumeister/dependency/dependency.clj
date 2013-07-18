(ns org.soulspace.build.baumeister.dependency.dependency
  (:use [org.soulspace.clj file]
        [org.soulspace.build.baumeister.repository artifact]
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
  (is-excluded? [dependency] "returns true if the artifact of this dependency is excluded")
  (includes [dependency])
  (excludes [dependency]))

(defprotocol DependencyPattern
  (matches-dependency? [pattern dependency]))

; Part of the dependency: artifact target scope dependencies exclusions
(defrecord DependencyImpl [artifact target scope dependencies exclusions]
  Dependency
  (is-excluded? [dependency]
                (exclude? (:artifact dependency)))
  (includes [dependency]
            (filter #(not (exclude? %)) dependencies))
  (excludes [dependency]
            (filter #(exclude? %) dependencies)))

; TODO add required signatures
(defn new-dependency
  ([artifact]
    (DependencyImpl. (new-artifact artifact) "runtime" nil [] []))
  ([artifact target]
    (DependencyImpl. (new-artifact artifact) target nil  [] []))
  ([artifact target dependencies]
    (DependencyImpl. (new-artifact artifact) target nil dependencies []))
  ([artifact target dependencies exclusions]
    (DependencyImpl. (new-artifact artifact) target nil dependencies exclusions))
  ([artifact target dependencies exclusions scope]
    (DependencyImpl. (new-artifact artifact) target scope dependencies exclusions)))
