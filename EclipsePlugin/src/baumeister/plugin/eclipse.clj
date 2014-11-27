;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.eclipse
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]
            [baumeister.plugin.eclipse.classpath-dsl :as cp])
  (:use [clojure.java.io :only [as-file]]
        [org.soulspace.clj file]
        [org.soulspace.clj.artifact artifact]
        [org.soulspace.clj.xml dsl-builder util zip]
        [baumeister.config registry]
        [baumeister.utils checks log]))

(def eclipse-source-dirs
  [(param "${source-dir}")
   (param "${source-unittest-dir}")
   (param "${source-integrationtest-dir}")
   (param "${source-acceptancetest-dir}")
   (param "${mdsd-generation-dir}/${source-dir}")
   (param "${mdsd-generation-dir}/${source-unittest-dir}")
   (param "${mdsd-generation-dir}/${source-integrationtest-dir}")
   (param "${mdsd-generation-dir}/${source-acceptancetest-dir}")])

(def eclipse-containers
  [["org.eclipse.jdt.launching.JRE_CONTAINER"]])

(defn build-container-entries []
  ; TODO zip from .classpath and include again?
  (let [zipper (xml-zipper (param "${module-dir}/.classpath"))
        ;zipper (xml-zipper (param "${module-dir}/data/.classpath"))
        nodes (map zip/node (zx/xml-> zipper :classpathentry [(zx/attr= :kind "con")]))]
    nodes))

(defn build-source-entries []
    (doall
      (->>
        (remove #(not (exists? %)) eclipse-source-dirs)
        (map #(apply hash-map [:kind "src" :path %]))
        (map #(cp/classpathentry %)))))
    
(defn code-dependency?
  "Returns true, if the depedency is a code library."
  [dep]
  (contains? #{:runtime :aspect :dev :aspectin} (:target dep)))

(defn lib-target-path [dep]
  (str (param (keyword (str "lib-" (name (:target dep)) "-dir"))) "/" (artifact-name (:artifact dep))))

(defn web-lib-entry [dep]
  (cp/classpathentry
    {:kind "lib"
     :path (lib-target-path dep)}
    (cp/attributes
      {}
      (cp/attribute {:name "org.eclipse.jst.component.dependency" :value "WEB-INF/lib"}))))

(defn lib-entry [dep]
  (cond 
    (= (:target dep) :aspect)
    (cp/classpathentry
      {:kind "lib" :path (lib-target-path dep)}
      (cp/attributes
        {}
        (cp/attribute {:name "org.eclipse.ajdt.aspectpath"
                       :value "org.eclipse.ajdt.aspectpath"})))
    :default
    (cp/classpathentry {:kind "lib" :path (lib-target-path dep)})))

(defn build-lib-entries []
  (let [deps (filter code-dependency? (param :dependencies-processed))]
    (if (web-module?)
      (map web-lib-entry deps)
      (map lib-entry deps))))

(defn build-classpath []
  (cp/classpath
    {}
    (build-source-entries)
    (build-container-entries)
    (build-lib-entries)
    (cp/classpathentry {:kind "output" :path "bin"})))

;
; workflow functions
;
(defn eclipse-init []
  (create-dir (as-file (param "${build-eclipse-dir}"))))

(defn eclipse-post-dependencies []
  (build-container-entries)
  (let [classpath (build-classpath)]
    (if (param :eclipse-test)
      (spit (param "${build-eclipse-dir}/classpath.xml") (xml/emit-str classpath))
      (spit (param "${module-dir}/.classpath") (xml/emit-str classpath)))))

(def config 
  {:params [[:build-eclipse-dir "${build-dir}/eclipse"]
            [:eclipse-test false]]
   :steps [[:init eclipse-init]
           [:post-dependencies eclipse-post-dependencies]]
   :functions []})
