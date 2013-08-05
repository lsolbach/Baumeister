;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.dependency.dependency
  (:use [org.soulspace.clj file]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.build.baumeister.config registry]))

(defn copy? [dependency]
  (contains? (:copy (param :dependency-actions)) (:target dependency)))

(defn unzip? [dependency]
  (contains? (:unzip (param :dependency-actions)) (:target dependency)))

(defn follow? [dependency]
  (contains? (:follow (param :dependency-actions)) (:target dependency)))

(def compatible-targets
  {:aspectj :runtime
   :aspectin :dev})

(defn print-dependency
  ([dependency]
    (print-dependency dependency (:target dependency)))
  ([dependency target]
    (let [artifact (:artifact dependency)]
      (str "[" 
           (clojure.string/join  ", " 
                                 [(:project artifact) (:module artifact) (artifact-version artifact) (:name artifact) (:type artifact) target])
           "]"))))
  
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
