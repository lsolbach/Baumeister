(ns baumeister.config.config
  (:require [clojure.string :as str :only [split join replace starts-with?]]
            [clojure.edn :as edn])
  (:use [clojure.pprint]
        [clojure.java.io :only [as-file as-url input-stream]]
        [org.soulspace.clj file string]
        [org.soulspace.clj.application classpath env-vars]
        [baumeister.utils log]
        [baumeister.config registry parameter-registry repository-registry function-registry plugin-registry]))

; TODO read the whole configuration before taking actions on specific keys (repositories, plugins,...)?!
; TODO conj all configuration items to a list and process them after reading all?! So the order would be preserved.

(defn- reset-registries
  "Resets the registries."
  []
  (reset-plugin-registry)
  (reset-fn-registries)
  (reset-param-registry))

(defn- read-module
  "Reads a module file and returns the content partitioned in key and value sequences."
  ([] (read-module "./module.clj"))
  ([file]
   (log :trace "Loading configuration from " (canonical-path file))
   (if (is-file? file)
     (partition 2 (edn/read-string (slurp file)))
     (message :info "Could not load configuration file " (canonical-path file) "."))))

(defn parse-define-option
  "Parses a defined command line option."
  [define]
  (let [[key value] (str/split define  #"[=:]")] ; TODO '=' is a split char in windows cmd
    [(keyword key) (edn/read-string value)]))

(defn get-params-from-options
  "Extracts the defined options from the command line args."
  [options]
  (let [define (:define options)
        params (cond
                 (nil? define) []
                 (string? define) (parse-define-option define)
                 (coll? define) (mapcat parse-define-option define)
                 :default [])]
    (partition 2 params)))

(defn param-action
  "Executes an action for the parameter based on the key."
  [key value]
  (log :trace "param action for" key "->" value)
  (cond
    ; repositories
    (= key :repositories) ; TODO append to existing repositories
    (do
      (message :info "registering repositories...")
      ; register repositories
      (register-val :deps-repositories (if (seq (param :deps-repositories))
                                         (let [repos (param :deps-repositories)]
                                           (into repos (create-repositories value)))
                                         (create-repositories value))))
    ; plugins
    (= key :plugins)
    (do
      (message :info "loading plugins...")
      ; plugin registration has to take place when the :plugins key is resolved in module.clj
      ; otherwise the plugin default config will override the module config
      ;
      ; IDEA An alternative is the specification of the module specific plugin configuration
      ; IDEA as part of the plugin dependency (like in maven).
      ;
      ; IDEA Another alternative is the building of the complete configuration as a data structure
      ; IDEA (vector of key/value pairs) over all the different configuration options
      ; IDEA before parsing the configuration like it is done now.
      (init-plugins value))
    (= key :log-level) (set-log-level (keyword value))
    (= key :message-level) (set-message-level (keyword value))))

(defn set-params
  "Sets parameters in the parameter registry."
  [params]
  (log :debug "set params" params)
  (if (seq params)
    (doseq [[key value] params]
      ; TODO implement override/append behaviour on defined keys?
      (log :trace "setting parameter" key "to" value)
      (if (str/starts-with? (name key) "additional-")
        ; if a key starts with additional, the values get appended to the collection of base values
        (let [param-key (substring (count "additional-") (name key))]
          (log :trace "updating parameter" key "with" value)
          (update-var param-key value)
          (param-action param-key value))
        (do
          (log :trace "setting parameter" key "to" value)
          (register-var key value)
          (param-action key value))))))

(defn read-from-file
  "Reads configuration from file."
  [filename]
  (read-module filename))

(defn read-from-options
  "Reads configuration from options."
  [options]
  (get-params-from-options options))

(defn read-from-seq
  "Adds configuration from coll."
  [coll]
  (partition 2 coll))

(defn configure-from-file
  "Adds configuration from file."
  [filename]
  (set-params (read-module filename)))

(defn configure-from-options
  "Adds configuration from options."
  [options]
  (set-params (get-params-from-options options)))

(defn configure-from-seq
  "Adds configuration from coll."
  [config]
  (set-params (partition 2 config)))

(defn init
  ""
  []
  (set-dynamic-classloader) ; ensure a dynamic classloader for modifying the plugin and clojure test classpaths
  (reset-registries)) ; always get a fresh environment if used in a repl's


(defn init-defaults
  "Initializes the configuration defaults."
  ([]
    ; set internal defaults
   (init-defaults [:baumeister-home-dir (get-env "BAUMEISTER_HOME" ".")
                   :user-home-dir (get-env "HOME" (get-env "USERPROFILE"))
                   :java-home (get-env "JAVA_HOME")
                   :aspectj-home (get-env "ASPECTJ_HOME")]))
  ([defaults]
   (println (partition 2 defaults))
   (configure-from-seq defaults)))


(defn init-config
  "Initializes the configuration."
  ([options]
   (init-config () options))
  ([config options]

  ; use given config
   (configure-from-seq config)

  ; default settings
   (configure-from-file (str (param :baumeister-home-dir) "/config/default_settings.clj"))

  ; user settings
   (configure-from-file (str (param :user-home-dir) "/.Baumeister/settings.clj"))

  ; read module.clj (or the file specified with --file or -f) from current module
  ; TODO for module.clj derivation get parent module.clj's and merge them first, requires repository access
   (configure-from-file (:file options))

  ; add command line parameters (defined by --define or -D)
   (configure-from-options options)))
