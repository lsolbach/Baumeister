;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.config.plugin-registry
  (:use [clojure.string :only [lower-case]]
        [org.soulspace.clj string namespace]
        [org.soulspace.clj.application classpath]
        [org.soulspace.clj.artifact artifact]
        [baumeister.config parameter-registry function-registry]))

(def plugin-ns-prefix "baumeister.plugin")
(def plugin-path "baumeister/plugin")

;
; plugin registry
;
(def ^{:dynamic true :private true} plugin-registry)

(defn reset-plugin-registry [] (def plugin-registry #{})) ; initialize plugin registry as empty set
(defn register-plugin [plugin] (def plugin-registry (conj plugin-registry plugin)))
(defn has-plugin? [plugin] ((set plugin-registry) plugin))

(defn plugin-ns-string [name]
  (str plugin-ns-prefix "." name))

(defn plugin-name
  [c]
  (lower-case (second (first (re-seq #"Baumeister(.*)Plugin" (:module (new-artifact (first c))))))))

; TODO load plugin as dependency? yes, when the build framework is stable
; load-file or require? (use compiled classes in Baumeister.jar and load-file user plugins from file system?)
(defn init-plugin [name]
  (let [plugin (symbol  name)]
    (when-not (has-plugin? plugin)
      (println "loading plugin" name)
      (require plugin) ; import plugin namespace
      (register-plugin name) ; register plugin in plugin registry
      (let [config-var (ns-resolve plugin (symbol "config"))]
        (register-params (:params @config-var))
        (register-functions (:functions @config-var))))))

;
(defn init-plugins [plugins]
  "initialize the given seq of plugins"
  ; get plugin dependencies for all plugins and set the classpath accordingly
  ; get plugin name and plugin artifact
  (doseq [plugin plugins]
    ; check if plugin is a plugin dependency
    (cond
      ; if not just load the plugin as clj file
      (string? plugin)
      (init-plugin (plugin-ns-string plugin))
      ; if so, resolve transitive dependencies
      (coll? plugin)
      (init-plugin (plugin-ns-string (plugin-name plugin))))))

