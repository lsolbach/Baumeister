(ns org.soulspace.build.baumeister.dependency.dependency
  (:use [org.soulspace.clj file]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.config registry]))

(defn copy? [dependency]
  (contains? (:copy (param :dependency-actions)) (:target dependency)))

(defn unzip? [dependency]
  (contains? (:unzip (param :dependency-actions)) (:target dependency)))

(def compatible-targets
  {:aspectj :runtime
   :aspectin :dev})

;
; Dependency Protocol 
;
(defprotocol Dependency
  (compatible-dependency? [this other] "Returns true if both dependencies are compatible."))

; Part of the dependency: artifact target scope dependencies exclusions
(defrecord DependencyImpl [artifact target optional scope exclusions]
  Dependency
  (compatible-dependency? [this other]
    (let [this-artifact (:artifact this)
          other-artifact (:artifact other)]
      (and (same-artifact-apart-from-version? this-artifact other-artifact)
           ;(= (contains-version? (:version a1) (:version a2)))
           (or (= (:target this) (:target other))
               (= (compatible-targets (:target this)) (:target other)))))))

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
