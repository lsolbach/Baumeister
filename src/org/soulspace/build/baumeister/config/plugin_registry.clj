(ns org.soulspace.build.baumeister.config.plugin-registry
  (:use [org.soulspace.clj function]))

(def plugin-ns-prefix "org.soulspace.build.baumeister.plugins")
(def plugin-path "org/soulspace/build/baumeister/plugins")

;
; plugin registry
;
(def ^{:dynamic true :private true} plugin-registry)

(defn reset-plugin-registry [] (def plugin-registry #{})) ; initialize plugin registry as empty set
(defn register-plugin [plugin] (def plugin-registry (conj plugin-registry plugin)))
(defn has-plugin? [plugin] ((set plugin-registry) plugin))

(defn plugin-ns-string [name]
  (str plugin-ns-prefix "." name))

(defn plugin-file [name]
  (str "src/" plugin-path "/" name ".clj"))

; TODO load plugin as dependency? yes, when the build framework is stable
; load-file or require? (use compiled classes in Baumeister.jar and load-file user plugins from file system?)
(defn init-plugin [name]
  (let [plugin (symbol (plugin-ns-string name))]
    (when-not (has-plugin? plugin)
      (println "loading plugin" name)
      (require (symbol (plugin-ns-string name))) ; import plugin namespace
      ; TODO register fns and vars from here by config instead of calling the plugin init method
      (let [config-var (ns-resolve plugin (symbol "config"))]
        ; (println @config-var)
        )
      (call-by-ns-name (plugin-ns-string name) "plugin-init") ; call plugin init in plugin namespace
      (register-plugin name)))) ; register plugin in plugin registry
  
(defn init-plugins [plugins]
  "initialize the given set of plugins"
  (doseq [plugin plugins]
    (init-plugin plugin)))

