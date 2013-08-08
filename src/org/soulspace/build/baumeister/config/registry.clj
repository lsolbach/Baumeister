;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.config.registry
  (:require [clojure.string :as str :only [join replace]])
  (:use [clojure.java.io :only [as-file as-url]]
        [org.soulspace.clj file]
        [org.soulspace.clj.application classpath env-vars]
        [org.soulspace.build.baumeister.config parameter-registry function-registry plugin-registry]))

; TODO returns a parameter as-is without property replacement. still needed? if so, choose new fn name
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

(defn get-classpath-urls
  []
  (classpath-urls))

(defn register-classpath-entries
  [cl-entries]
  (let [urls (into #{} (get-classpath-urls))]
    (doseq [entry cl-entries]
      (let [url (as-url (canonical-file entry))]
        (if-not (contains? urls url)
          (add-classpath-url url))))))
  
(defn- reset-registries []
  "Reset the registries."
  (reset-plugin-registry)
  (reset-fn-registry)
  (reset-param-registry))

(defn- read-module
  "Read a module file and returns the content partitioned in key and value sequences."
  ([] (read-module "./module.clj"))
  ([file] (partition 2 (load-file file))))

(defn get-params [options]
  (let [define (:define options)
        params (cond
                 (nil? define) []
                 (string? define) (let [[key value] (clojure.string/split define  #"=")]
                                    [(keyword key) (read-string value)])
                 (coll? define) (loop [defs define params []]
                                  (if-not (seq defs)
                                    (let [def (first defs)
                                          [key value] (clojure.string/split def  #"=")]
                                      (recur (rest defs) (conj params [(keyword key) (read-string value)])))
                                    params))
                 :default [])]
    (partition 2 params)))

(defn set-params [params]
  (doseq [[key value] params]
    (when (= key :plugins)
      (init-plugins value))
    (register-var key value)))

(defn init-config [options]
  (set-dynamic-classloader) ; ensure an dynamic classloader
  (reset-registries) ; for use with repl's
  ;(println "Options" options)
  
  (register-var :baumeister-home-dir (get-env "BAUMEISTER_HOME" ".")) ; register baumeister-home-dir
  (register-var :user-home-dir (get-env "HOME")) ; register baumeister-home-dir
  (register-var :java-home (get-env "JAVA_HOME")) ; register JAVA_HOME
  (register-var :aspectj-home (get-env "ASPECTJ_HOME")) ; register ASPECTJ_HOME
  (register-var :log-level :error) ; initial log level for intialization
  
  ; set module defaults
  (set-params (read-module (str (param :baumeister-home-dir) "/config/module_defaults.clj")))

  ; get config path and read configs from path for e.g. user settings
  (try
    (set-params (read-module (str (param :user-home-dir) "/.Baumeister/settings.clj")))
    (catch java.io.IOException e))
  
  ; read module.clj (or the file specified with --file or -f) from current module
  ; TODO get parent module.clj's and merge them first, requires repository access
  (set-params (read-module (:file options)))
  
  ; add command line parameters (defined by --define or -D)
  (set-params (get-params options))
  )
