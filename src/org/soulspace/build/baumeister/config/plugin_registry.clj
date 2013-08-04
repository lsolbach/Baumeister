(ns org.soulspace.build.baumeister.config.plugin-registry
  (:use [org.soulspace.clj function]
        [org.soulspace.build.baumeister.utils.classpath]
        [org.soulspace.build.baumeister.config parameter-registry function-registry]))

(def plugin-ns-prefix "org.soulspace.build.baumeister.plugins")
(def plugin-path "org/soulspace/build/baumeister/plugins")

;
; plugin registry
;
(def ^{:dynamic true :private true} plugin-registry)

(defn add-classpath-url
  "Adds an URL to the classpath."
  [url]
  (add-url url))

(defn classpath-urls
  "Returns the URLs currently added to the classpath."
  []
  (urls))

(defn reset-plugin-registry [] 
  (def plugin-classloader nil)
  (def plugin-registry #{})) ; initialize plugin registry as empty set
(defn register-plugin [plugin] (def plugin-registry (conj plugin-registry plugin)))
(defn has-plugin? [plugin] ((set plugin-registry) plugin))

(defn plugin-ns-string [name]
  (str plugin-ns-prefix "." name))

; TODO load plugin as dependency? yes, when the build framework is stable
; load-file or require? (use compiled classes in Baumeister.jar and load-file user plugins from file system?)
(defn init-plugin [name]
  (let [plugin (symbol (plugin-ns-string name))]
    (when-not (has-plugin? plugin)
      (println "loading plugin" name)
      (require (symbol (plugin-ns-string name))) ; import plugin namespace
      (register-plugin name) ; register plugin in plugin registry
      (let [config-var (ns-resolve plugin (symbol "config"))]
        (register-params (:params @config-var))
        (register-functions (:functions @config-var))))))

(defn init-plugins [plugins]
  "initialize the given set of plugins"
  (doseq [plugin plugins]
    (init-plugin plugin)))
