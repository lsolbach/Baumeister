(ns org.soulspace.build.baumeister.utils.dependency
  (:use [org.soulspace.clj.lib file]
        [org.soulspace.build.baumeister.config registry]))


(def dep-config
  (load-file (str (get-home) "/config/dependency_defaults.clj")))

(defn copy? [artifact]
  (contains? (:copy (:actions dep-config)) (:target artifact)))

(defn unzip? [artifact]
  (contains? (:unzip (:actions dep-config)) (:target artifact)))

(defn follow? [artifact]
  (contains? (:follow (:actions dep-config)) (:target artifact)))

(defn exclude? [artifact]
  (contains? (:exclude (:actions dep-config)) (:target artifact)))

(defprotocol DependencyNode
  (is-excluded? [node] "returns true if the artifact of this dependency is excluded")
  (includes [node])
  (excludes [node]))

(defrecord DependencyNodeImpl [artifact dependencies]
  DependencyNode
  (is-excluded? [node]
                (exclude? (:artifact node)))
  (includes [node]
            (filter #(not (exclude? %)) dependencies))
  (excludes [node]
            (filter #(exclude? %) dependencies)))

(defn new-dependency-node
  ([artifact dependencies]
    (DependencyNodeImpl. artifact dependencies)))
