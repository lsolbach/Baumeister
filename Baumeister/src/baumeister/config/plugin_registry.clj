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
        [baumeister.utils log]
        [baumeister.config parameter-registry function-registry]
        [baumeister.dependency dependency dependency-transitivity dependency-initialization]))

;
; plugin registry
;
(def ^{:dynamic true :private true} plugin-registry)

(defn reset-plugin-registry [] (def plugin-registry #{})) ; initialize plugin registry as empty set
(defn register-plugin [plugin] (def plugin-registry (conj plugin-registry plugin)))
(defn has-plugin? [plugin] ((set plugin-registry) plugin))

; TODO the plugin namespace is too static, use project and module params from plugin dependencies
(def plugin-ns-prefix "baumeister.plugin")
(def plugin-path "baumeister/plugin")

(defn plugin-ns-string
  "Returns the qualified namespace for the plugin."
  [name]
  (str plugin-ns-prefix "." name))

(defn plugin-name
  "Extracts the plugin namespace name from the plugin artifact"
  [c]
  (lower-case (second (first (re-seq #"(.*)Plugin" (:module (new-artifact (first c))))))))

(defn set-plugin-dependency-classpath
  "Adds the plugin dependencies to the baumeister classpath."
  []
  ; get the urls for the plugin dependencies, but drop plugin root, because it's the current module
  (let [plugin-deps (plugin-dependencies)
        plugin-dependency-urls (artifact-urls (filter #(not= (:target %) :plugin-root) plugin-deps))]
    (log :trace "plugin dependencies" plugin-deps)
    (generate-plugin-dot)
    (add-urls plugin-dependency-urls)))

; TODO load plugin as dependency? yes, when the build framework is stable
; load-file or require? (use compiled classes in Baumeister.jar or Baumeister plugins and load-file user plugins from file system?)
(defn init-plugin
  [name]
  (let [plugin (symbol  name)]
    (when-not (has-plugin? plugin)
      (println "loading plugin" name)
      (require plugin) ; import plugin namespace
      (register-plugin name) ; register plugin in plugin registry
      (let [config-var (ns-resolve plugin (symbol "config"))]
        (register-params (:params @config-var))
        (register-steps (:steps @config-var))
        (register-functions (:functions @config-var))))))

;
(defn init-plugins
  "initialize the given seq of plugins"
  [plugins]

  ; get plugin dependencies for all plugins and set the classpath accordingly
  (set-plugin-dependency-classpath)
  ; initialize all plugins
  (doseq [plugin plugins]
    (cond
      ; if it's an internal plugin just use the string as name
      (string? plugin) (init-plugin (plugin-ns-string plugin))
      ; if it's a plugin dependency build the name from the dependency data
      (coll? plugin) (init-plugin (plugin-ns-string (plugin-name plugin))))))
