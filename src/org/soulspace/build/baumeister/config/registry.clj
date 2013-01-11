(ns org.soulspace.build.baumeister.config.registry
  (:require [clojure.string :as str :only [replace]])
  (:use [org.soulspace.clj.lib function]))

(def home-dir (get-env "HOME"))
(def home (get-env "BAUMEISTER_HOME" (str home-dir "/devel/Baumeister")))
(defn get-home [] home)
(defn get-lib-dir [] (str home "/lib"))

(def plugin-ns-prefix "org.soulspace.build.baumeister.plugins")
(def plugin-path "org/soulspace/build/baumeister/plugins")

(defn workflow-phases []
  (load-file (str home "/config/workflow_defaults.clj")))

; TODO rethink these registries!!!

; registry for plugins
(def ^:dynamic plugin-registry)

(defn reset-plugin-registry [] (def plugin-registry #{}))
(defn register-plugin [plugin] (def plugin-registry (conj plugin-registry plugin)))
(defn has-plugin? [plugin] ((set plugin-registry) plugin))

(defn plugin-ns-string [name]
  (str plugin-ns-prefix "." name))

(defn plugin-file [name]
  (str "src/" plugin-path "/" name ".clj"))

; TODO load plugin as dependency?
(defn init-plugin [name]
  (let [plugin (symbol (plugin-ns-string name))]
    (println "loading plugin" name)
    ; load-file or use? (use compiled classes in Baumeister.jar and load-file user plugins from file system?)
    (require (symbol (plugin-ns-string name)))
    (call-by-ns-name (plugin-ns-string name) "plugin-init")
    (register-plugin name)))
  
(defn init-plugins [plugins]
  (doseq [plugin plugins]
    (init-plugin plugin)))

(def ^:dynamic fn-registry) ; registry for plugin functions

(defn reset-fn-registry [] (def fn-registry {}))
(defn register-fn [build-step function]
  (let [step (keyword build-step)]
    (def fn-registry
      (assoc fn-registry step (conj (get fn-registry step []) function)))))

(defn register-fns [fns]
  (doseq [[build-step function] fns]
    (register-fn build-step function)))

(def ^:dynamic var-registry) ; registry for variables

(defn reset-var-registry [] (def var-registry {}))

(declare replace-vars)

(defn register-val [key value]
  "Register the key/value pair without any preprocessing"
  (def var-registry (assoc var-registry key value)))

; TODO refactor to multimethod
; TODO merge seqs/vectors if a seq/vector var is already registered?!?
; TODO  to e.g. handle default repositories in module_defaults.clj and additional module specific repositories
; TODO check if this is a desired behaviour for all seq/vector vars (always add, never override?)
(defn register-var [key value]
  "Register the key/value pair with preprocessing (e.g. variable replacement)"
  (def var-registry
    (cond
      (string? value)
      (assoc var-registry key (replace-vars value))
      (vector? value)
      (assoc var-registry key (map replace-vars value))
      (seq? value)
      (assoc var-registry key (map replace-vars value))
      :default
      (do 
        ;(println "default!" (type value))
        (assoc var-registry key value)))))

; TODO support documentation on vars
(defn register-vars [vars]
  (doseq [[key value] vars]
    (register-var key value)))

; concatenate the tokens matched by the pattern of replace vars
(defn concat-tokens [vars [_ t1 t2 t3]]
  (str t1 (get vars (keyword t2) (str "${" t2 "}")) t3))

; replace "${build-dir}/report" with (str (get-var (keyword build-dir) "${build-dir}") "/dir") (TODO: recursivly?)
(defn replace-vars
  ([vars value]
    (cond
      (string? value)
      (if-let [tokens (re-seq #"([^$]*)(?:\$\{([^}]*)\}*([^$]*))" value)]
        (reduce str (map (partial concat-tokens vars) tokens))
        value)
      (coll? value)
      (map (partial replace-vars vars )value)
      :default
      value))
  ([value]
    (replace-vars var-registry value)))

(defn get-var 
  ([name] (get var-registry name ""))
  ([name default] (get var-registry name default)))

(defn param 
  ([k]
    (if (keyword? k)
      (get-var k)
      (replace-vars k)))
  ([k default]
    (if (keyword? k)
      (get-var k default)
      (replace-vars k))))

(defn reset-registries []
  (reset-plugin-registry)
  (reset-fn-registry)
  (reset-var-registry))

(defn read-module
  ([file] (partition 2 (load-file file)))
  ([] (read-module "./module.clj")))

(defn init-config []
  (reset-registries) ; for use with repl's
  (register-var :baumeister-home-dir (get-home)) ; register baumeister-home-dir
  (doseq [[key value] (read-module (str home "/config/module_defaults.clj"))]
    (register-var key value))
  (doseq [[key value] (read-module)]
    (when (= key :plugins)
      (init-plugins value))
    (register-var key value)))
