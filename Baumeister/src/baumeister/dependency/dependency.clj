;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.dependency.dependency
  (:require [clojure.string :as str]
            [org.soulspace.clj.file :as file]
            [org.soulspace.tools.artifact :as artifact]
            [baumeister.config.registry :as reg]))

(defn copy?
  "Returns true if the dependency has to be copied."
  [dependency]
  (contains? (:copy (reg/param :dependency-actions)) (:target dependency)))

(defn unzip?
  "Returns true if the dependency has to be unzipped."
  [dependency]
  (contains? (:unzip (reg/param :dependency-actions)) (:target dependency)))

(defn follow?
  "Returns true if the dependency has to be followed only."
  [dependency]
  (contains? (:follow (reg/param :dependency-actions)) (:target dependency)))

(def compatible-targets
  {:aspectj :runtime
   :aspectin :dev})

(defn set-dependency-target
  "Sets the target of the dependency."
  [dependency target]
  (assoc dependency :target target))

(defn print-artifact
  "Prints an artifact."
  ([artifact]
    (str "[" 
         (str/join ", " [(:project artifact) (:module artifact) (artifact/artifact-version artifact) (:name artifact) (:type artifact)])
         "]")))

(defn print-dependency
  "Prints a dependency."
  ([dependency]
    (print-dependency dependency (:target dependency)))
  ([dependency target]
    (let [artifact (:artifact dependency)]
      (str "[" (str/join ", " [(print-artifact artifact) target (:optional dependency)]) "]"))))

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
      (and (artifact/same-artifact-apart-from-version? this-artifact other-artifact)
           ;(= (contains-version? (:version a1) (:version a2)))
           (or (= (:target this) (:target other))
               (= (compatible-targets (:target this)) (:target other)))))))

; TODO map POM scopes and types here?! (are currently mapped while building the dependency tree)
; add signatures as required
(defn create-dependency
  "Creates a new dependency to the given artifact."
  ([artifact]
    (DependencyImpl. (artifact/new-artifact artifact) :runtime false nil []))
  ([artifact target]
    (DependencyImpl. (artifact/new-artifact artifact) target false nil []))
  ([artifact target exclusions]
    (DependencyImpl. (artifact/new-artifact artifact) target false nil (map artifact/new-artifact-pattern exclusions)))
  ([artifact target optional exclusions]
    (DependencyImpl. (artifact/new-artifact artifact) target optional nil (map artifact/new-artifact-pattern exclusions)))
  ([artifact target optional scope exclusions]
    (DependencyImpl. (artifact/new-artifact artifact) target optional scope (map artifact/new-artifact-pattern exclusions))))

(defn new-dependency
  "Creates a new dependency."
  [& args]
  (apply create-dependency args))
