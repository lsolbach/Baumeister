(ns org.soulspace.build.baumeister.plugins.publish
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.build.baumeister.utils ant-utils checks log]
        [org.soulspace.build.baumeister.config registry]))

(defn publish-dist []
  ; TODO rethink: binary release package?!
  ; TODO for what module types do we need a binary distribution (webfrontends, appfrontends, webservices, consolefrontends)?
  ; TODO should binary packages only be build for releases?
  ; TODO in release or publish or ... plugin?
  (let [filename (param "${module}-${version}.zip")]
    (ant-zip {:destFile (str (param :dist-dir) "/" filename)}
             (ant-fileset {:dir (param :dist-dir) :includes "*.jar"})
             ;(ant-zipfileset {:dir (param :lib-runtime-dir) :includes "*.jar" :prefix "/lib"})
             ;(when (plugin? "aspectj")
             ;  (ant-zipfileset {:dir (param :lib-aspect-dir) :includes "*.jar" :prefix "/lib"}))
             (ant-fileset {:dir (param :module-dir) :includes "module.clj"})
             (ant-fileset {:dir (param :module-dir) :includes "README.md"}))
    ; FIXME copy binary release package to some useful repository
    (copy (as-file (str (param :dist-dir) "/" filename)) (as-file (str "../" filename)))))

(defn plugin-init []
  (log :info  "initializing plugin publish")
  (register-fn :distribute publish-dist))
