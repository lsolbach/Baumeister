(ns org.soulspace.build.baumeister.config.registry
  (:require [clojure.string :as str :only [join replace]])
  (:use [org.soulspace.clj function]
        [org.soulspace.build.baumeister.config parameter-registry function-registry plugin-registry]))

; TODO still needed? if so, choose new fn name
(defn get-var 
  "Get parameter without replacements."
  ([name] (get (get-param-registry) name ""))
  ([name default] (get (get-param-registry) name default)))

(defn param
  "Get parameter with replacements."
  ([k]
    (if (keyword? k)
      (get-var k)
      (replace-vars k)))
  ([k default]
    (if (keyword? k)
      (get-var k default)
      (replace-vars k))))

; TODO used in plugins for the plugin class path, refactor when plugins are dependencies 
; Baumeister lib dir
(defn get-lib-dir [] (str (param :baumeister-home-dir) "/lib"))

; Baumeister lib path
(defn lib-path [coll]
  (str/join ":" (map #(str (get-lib-dir) "/" % ".jar") coll)))

; TODO remove register methods, use data from plugins and register the data from plugin-registry
(defn register-fn [step fn]
  (register-function step fn))

(defn register-fns [fns]
  (register-functions fns))

(defn register-val [key value]
  (register-param-as-is key value))

(defn register-var [key value]
  (register-param key value))

(defn register-vars [vars]
  (register-params vars))


(defn- reset-registries []
  "Reset the registries."
  (reset-plugin-registry)
  (reset-fn-registry)
  (reset-param-registry))

(defn- read-module
  "Read a module file and returns the content partitioned in key and value sequences."
  ([] (read-module "./module.clj"))
  ([file] (partition 2 (load-file file))))

; TODO add param-map from options parsing
(defn init-config []
  (reset-registries) ; for use with repl's
  
  (register-var :baumeister-home-dir (get-env "BAUMEISTER_HOME" ".")) ; register baumeister-home-dir
  (register-var :user-home-dir (get-env "HOME")) ; register baumeister-home-dir

  (doseq [[key value] (read-module (str (param :baumeister-home-dir) "/config/module_defaults.clj"))]
    (register-var key value))
  ; TODO get config path and read configs from path for e.g. user settings
  
  ; read module.clj from current module ; TODO get parent module.clj's and merge them first, requires repository access
  (doseq [[key value] (read-module)]
    (when (= key :plugins)
      (init-plugins value))
    (register-var key value))

  ; TODO add command line parameters
  )
