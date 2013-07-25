(ns org.soulspace.build.baumeister.dependency.dependency
  (:use [org.soulspace.clj file]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.config registry]))

(defn copy? [dependency]
  (contains? (:copy (param :dependency-actions)) (:target dependency)))

(defn unzip? [dependency]
  (contains? (:unzip (param :dependency-actions)) (:target dependency)))

;
; 
;
(defprotocol Dependency
  ; TODO functions
  )

; Part of the dependency: artifact target scope dependencies exclusions
(defrecord DependencyImpl [artifact target optional scope exclusions]
  Dependency
  ; TODO functions
  )

; TODO add required signatures
(defn new-dependency
  ([artifact]
    (DependencyImpl. (new-artifact artifact) :runtime false nil []))
  ([artifact target]
    (DependencyImpl. (new-artifact artifact) target false nil []))
  ([artifact target exclusions]
    (DependencyImpl. (new-artifact artifact) target false nil (map new-artifact-pattern exclusions)))
  ([artifact target optional exclusions]
    (DependencyImpl. (new-artifact artifact) target optional nil (map new-artifact-pattern exclusions)))
  ([artifact target optional scope exclusions]
    (DependencyImpl. (new-artifact artifact) target optional scope (map new-artifact-pattern exclusions))))

;
; Dependency pattern
; TODO needed?
;
(defprotocol DependencyPattern
  (matches-dependency? [pattern dependency]))
