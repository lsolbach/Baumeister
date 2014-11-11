(ns baumeister.config.config
  (:require [clojure.string :as str :only [split join replace]])
  (:use [clojure.pprint]
        [clojure.java.io :only [as-file as-url]]
        [org.soulspace.clj file]
        [org.soulspace.clj.application classpath env-vars]
        [baumeister.utils log]
        [baumeister.config registry parameter-registry repository-registry function-registry plugin-registry]))

; TODO read the whole configuration before taking actions on specific keys (repositories, plugins,...)?!
; TODO conj all configuration items to a list and process them after reading all?! So the order would be preserved.
; TODO define key syntax to which data gets appended instead of overriding the data from prior config

(defn- reset-registries []
  "Resets the registries."
  (reset-plugin-registry)
  (reset-fn-registries)
  (reset-param-registry))

(defn- read-module
  "Reads a module file and returns the content partitioned in key and value sequences."
  ([] (read-module "./module.clj"))
  ([file]
    (if (is-file? file)
      (partition 2 (load-file file))
      (message :info "Could not load configuration file " (file-name file) "."))))

(defn parse-define-option
  "Parses a defined command line option."
  [define]
  (let [[key value] (str/split define  #"=")]
    [(keyword key) (read-string value)]))

(defn get-params-from-options [options]
  "Extracts the defined options from the command line args."
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
  "Sets parameters in the parameter registry."
  (log :debug "set params" params)
  (if (seq params)
    (doseq [[key value] params]
      ; TODO implement override/append behaviour on defined keys?
      (log :trace "setting variable" key "to" value)
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
        (= key :log-level) (set-message-level (keyword value))))))

(defn init-defaults
  "Initializes the configuration defaults."
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
  "Initializes the configuration."
  [options]
  (init-defaults)
  
  ; get config path and read configs from path for e.g. user settings
  (set-params (read-module (str (param :user-home-dir) "/.Baumeister/settings.clj")))
  
  ; read module.clj (or the file specified with --file or -f) from current module
  ; TODO for module.clj derivation get parent module.clj's and merge them first, requires repository access
  (set-params (read-module (:file options)))
  
  ; add command line parameters (defined by --define or -D)
  (set-params (get-params-from-options options))
  (log :trace "registered parameters:")
  (log :trace (pprint (get-param-registry))))