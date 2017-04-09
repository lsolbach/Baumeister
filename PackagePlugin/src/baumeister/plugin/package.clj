;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.package
  (:use [clojure.java.io :only [as-file]]
        [org.soulspace.clj file function]
        [baumeister.utils ant-utils checks log]
        [baumeister.config registry]))

; TODO get file names from artifacts

; checks plugin mdsd, web-module-type, tests
(defn get-environments []
  (files (param :package-environment-dir)))

(defn manifest
  [dir additional-entries]
  (create-dir (as-file dir))
  (ant-manifest {:file (str dir "/MANIFEST.MF")}
                (merge {"Implementation-Version" (param :version)} additional-entries)))

(defn local-path
  [dir]
  ((partial relative-path (param :module-dir)) dir))

(defn package-jar
  ([dir jar-type]
    (package-jar dir jar-type "jar" {}))
  ([dir jar-type archive-type additional-manifest-entries]
    (log :debug  "packaging jar" dir " " jar-type " " archive-type)
    (manifest dir additional-manifest-entries)
    (ant-jar {:destFile (str (param "${dist-dir}/${module}") jar-type "." archive-type) :manifest (str dir "/MANIFEST.MF")}
             (ant-fileset {:dir dir}))))

(defn package-war
  [dir environment additional-manifest-entries]
  (log :debug  "packaging war" dir " " environment)
  ; TODO generate environment specific configuration
  ; generate environment specific war file
  (apply (partial ant-war {:destFile (str dir "/" environment "/" (param :context-name (param :module)) ".war" )
            :webxml (param "${source-webcontent-dir}/WEB-INF/web.xml")})
         (filter (complement nil?)
                 [(ant-zipfileset {:dir (param :dist-dir) :includes (param "${dist-dir}/${module}.jar") :prefix "WEB-INF/lib"})
                  (ant-zipfileset {:dir (param :lib-runtime-dir) :prefix "WEB-INF/lib"})
                  (when (plugin? "aspectj")
                    (ant-zipfileset {:dir (param :lib-aspect-dir) :prefix "WEB-INF/lib"}))
                  (ant-fileset {:dir "${source-webcontent-dir}"})
                  (when (plugin? "mdsd")
                    (ant-fileset {:dir (param "${mdsd-generation-dir}/WebContent")}))
                  ])))

(defn package-ear
  [dir environment additional-manifest-entries]
  (log :debug "packaging ear" dir " " environment)
  (log :debug "packaging ear is not implemented yet!")
  )

(defn package-sourcedoc
  []
  (ant-jar {:destFile (str (param "${dist-dir}/${module}") "Javadoc.jar")}
            (ant-fileset {:dir (param :build-sourcedoc-dir)})))

(defn package-sources
  []
  (ant-jar {:destFile (str (param "${dist-dir}/${module}") "Source.jar")}
            (ant-fileset {:dir (param :source-dir)})
            ;(ant-fileset {:dir (param :generated-source-dir)}) ; etc
            ))

(defn package-app
  []
  )

(defn package-data
  "Package data."
  []
  (log :debug  "packaging data" (param :dist-dir))
  (ant-zip {:destFile (param "${dist-dir}/${module}.zip")}
           (ant-fileset {:dir (param :module-dir)
                         :excludes (str (local-path (param :build-dir)) "/** bin/** .settings/** module.clj README.md .project .classpath license.txt")})))

; TODO handle source jars
(defn package-jars
  "Package JARs."
  []
  (package-jar (param :build-classes-dir) "" "jar" {})
  (when (unittest?)
    (package-jar (param :build-unittest-classes-dir) "Unittest" "jar" {}))
  (when (integrationtest?)
    (package-jar (param :build-integrationtest-classes-dir) "Integrationtest" "jar" {}))
  (when (acceptancetest?)
    (package-jar (param :build-acceptancetest-classes-dir) "Acceptancetest" "jar" {}))
  (when (is-dir? (as-file (param :build-sourcedoc-dir)))
    (package-sourcedoc))
  (when (seq (param :package-additional-jars))
    (doseq [[dir archive-type] (param :package-additional-jars)]
      (package-jar dir archive-type)))
  )

(defn package-package
  "Package package."
  []
  (when (code-module?)
    (package-jars))
  (when (and (web-module?) (package-war?))
    ; FIXME iterate over environments instead of using hardcoded "dev"
    (package-war (param :dist-dir) "dev" {}))
  (when (package-ear?)
    ; (package-ear)
    )
  (when (app-module?)
    ; TODO package app with start scripts
    )
  (when (console-module?)
    ; TODO package console app with start scripts
    )
  (when (data-module?)
    (package-data)))

(def config
  {:params [[:package-environment-dir "${module-dir}/env"]
            [:package-additional-jars []]]
   :steps [[:package package-package]]
   :functions []})
