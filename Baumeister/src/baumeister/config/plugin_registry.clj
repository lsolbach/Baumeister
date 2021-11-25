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
  (:require [clojure.string :as str]
            [org.soulspace.clj.application.classpath :as cp]
            [org.soulspace.tools.artifact :as artifact]
            [baumeister.utils.log :as log]
            [baumeister.config.parameter-registry :as preg]
            [baumeister.config.function-registry :as freg]
            [baumeister.dependency.dependency-transitivity :as dep-t]
            [baumeister.dependency.dependency-initialization :as dep-i]))

;;;
;;; plugin registry
;;;
(def plugin-registry (atom #{}))

(defn get-plugin-registry
  "Returns the plugin registry."
  []
  @plugin-registry)

(defn reset-plugin-registry
  "Resets the plugin registry."
  []
  (reset! plugin-registry #{})) ; initialize plugin registry as empty set

(defn register-plugin
  "Registers a plugin in the plugin registry."
  [plugin]
  (swap! plugin-registry conj plugin))

(defn has-plugin?
  "Returns true if the plugin is registered in the plugin registry."
  [plugin]
  (@plugin-registry plugin))

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
  (str/lower-case (second (first (re-seq #"(.*)Plugin" (:module (artifact/new-artifact (first c))))))))

(defn set-plugin-dependency-classpath
  "Adds the plugin dependencies to the baumeister classpath."
  []
  ; get the urls for the plugin dependencies, but drop plugin root, because it's the current module
  (let [plugin-deps (dep-t/plugin-dependencies)
        plugin-dependency-urls (dep-i/artifact-urls (filter #(not= (:target %) :plugin-root) plugin-deps))]
    (log/log :debug "plugin dependencies" plugin-deps)
    ;(generate-plugin-dot)
    (cp/add-urls plugin-dependency-urls)))

(defn get-plugin-config
  "Resolves the given plugin namespace and returns the config of the plugin."
  [plugin]
  (deref (ns-resolve plugin (symbol "config"))))

(defn register-plugin-parameters
  "Registers the plugin parameters."
  [plugin]
  (if-let [cfg (get-plugin-config plugin)]
    (preg/register-params (:params cfg))))

(defn register-plugin-stepss
  "Registers the plugin steps."
  [plugin]
  (if-let [cfg (get-plugin-config plugin)]
    (freg/register-steps (:steps cfg))))

(defn register-plugin-functions
  "Registers the plugin functions."
  [plugin]
  (if-let [cfg (get-plugin-config plugin)]
    (freg/register-functions (:functions cfg))))

; TODO load plugin as dependency? yes, when the build framework is stable
; load-file or require? (use compiled classes in Baumeister.jar or Baumeister plugins and load-file user plugins from file system?)
(defn init-plugin
  "Initializes a plugin."
  [name]
  (log/log :debug "Initializing plugin" name)
  (let [plugin (symbol  name)]
    (when-not (has-plugin? plugin)
      (log/log :debug "loading plugin" name)
      (require plugin) ; import plugin namespace
      (register-plugin name) ; register plugin in plugin registry
      ; TODO use functions or extract registration
      (let [config-var (ns-resolve plugin (symbol "config"))]
        (preg/register-params (:params @config-var))
        (freg/register-steps (:steps @config-var))
        (freg/register-functions (:functions @config-var))))))

;
(defn init-plugins
  "Initializes the given sequence of plugins."
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
