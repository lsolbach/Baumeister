(ns baumeister.config.config
  (:require [clojure.string :as str :only [split join replace]])
  (:use [clojure.java.io :only [as-file as-url]]
        [org.soulspace.clj file]
        [org.soulspace.clj.application classpath env-vars]
        [baumeister.utils log]
        [baumeister.config registry parameter-registry repository-registry function-registry plugin-registry]))

(defn- reset-registries []
  "Reset the registries."
  (reset-plugin-registry)
  (reset-fn-registries)
  (reset-param-registry))

(defn- read-module
  "Read a module file and returns the content partitioned in key and value sequences."
  ([] (read-module "./module.clj"))
  ([file]
    (if (is-file? file)
      (partition 2 (load-file file))
      (:message :info "Directory contains no module file."))))

(defn parse-define-option
  [define]
  (let [[key value] (str/split define  #"=")]
    [(keyword key) (read-string value)]))

(defn get-params-from-options [options]
  (let [define (:define options)
        params (cond
                 (nil? define) []
                 (string? define) (parse-define-option define)
                 (coll? define) (loop [defs define params []]
                                  (if (seq defs)
                                    (recur (rest defs) (conj params (parse-define-option (first defs))))
                                    params))
                 :default [])]
    (partition 2 params)))

(defn set-params [params]
  (doseq [[key value] params]
    ; TODO implement override/append behaviour on defined keys?
    (register-var key value)
    (cond
      (= key :repositories) ; TODO append to existing repositories
      (do
        (message :info "registering repositories...")
        ; register repositories
        (register-val :deps-repositories (create-repositories (param :repositories))))
      (= key :plugins)
      (do 
        (message :info "loading plugins...")
        ; plugin registration has to take place when the :plugins key is resolved in module.clj
        ; otherwise the plugin default config will overide the module config
        ; IDEA An alternative is the specification of the module specific plugin configuration
        ; IDEA as part of the plugin dependency (like in maven).
        (init-plugins (param :plugins)))
      (= key :log-level) (set-log-level (keyword value))
      (= key :log-level) (set-message-level (keyword value))
      )
    (log :trace "setting variable" key "to" value)
    ))

(defn init-defaults
  []
  (set-dynamic-classloader) ; ensure an dynamic classloader
  (reset-registries) ; for use with repl's

  ; set internal defaults  
  (register-var :baumeister-home-dir (get-env "BAUMEISTER_HOME" ".")) ; register baumeister-home-dir
  (register-var :user-home-dir (get-env "HOME")) ; register baumeister-home-dir
  (register-var :java-home (get-env "JAVA_HOME")) ; register JAVA_HOME
  (register-var :aspectj-home (get-env "ASPECTJ_HOME")) ; register ASPECTJ_HOME

  ; load module defaults
  (set-params (read-module (str (param :baumeister-home-dir) "/config/module_defaults.clj"))))

(defn init-config
  [options]
  (init-defaults)
  
  ; get config path and read configs from path for e.g. user settings
  (set-params (read-module (str (param :user-home-dir) "/.Baumeister/settings.clj")))
  
  ; read module.clj (or the file specified with --file or -f) from current module
  ; TODO for module.clj derivation get parent module.clj's and merge them first, requires repository access
  (set-params (read-module (:file options)))
  
  ; add command line parameters (defined by --define or -D)
  (set-params (get-params-from-options options))
  )
