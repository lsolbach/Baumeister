(ns org.soulspace.build.baumeister.plugins.release
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.build.baumeister.utils ant-utils checks log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

; TODO rethink: binary release package?!
; TODO for what module types do we need a binary distribution (webfrontends, appfrontends, webservices, consolefrontends)?
(defn release-package []
  (let [filename (param :release-file)]
    (ant-zip {:destFile (str (param :dist-dir) "/" filename)}
             (ant-fileset {:dir (param :dist-dir) :includes "*.jar"})
             ;(ant-zipfileset {:dir (param :lib-runtime-dir) :includes "*.jar" :prefix "/lib"})
             ;(when (plugin? "aspectj")
             ;  (ant-zipfileset {:dir (param :lib-aspect-dir) :includes "*.jar" :prefix "/lib"}))
             (ant-fileset {:dir (param :module-dir) :includes "module.clj"})
             (ant-fileset {:dir (param :module-dir) :includes "README.md"}))))


(defn release-distribute []
  (let [filename (param :release-file)]
    ; FIXME copy binary release package to some useful repository
    (copy (as-file (str (param :dist-dir) "/" filename)) (as-file (str "../" filename)))))

(def release-config
  {:params [[:release-file (param "${module}-${version}.zip")]]
   :functions [[:package-release release-package]
               [:distribute-release release-distribute]]})

(defn plugin-init []
  (log :info  "initializing plugin publish")
  (register-fns (:functions release-config)))
