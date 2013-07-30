(ns org.soulspace.build.baumeister.plugins.package
  (:use [org.soulspace.clj file function]
        [org.soulspace.build.baumeister.utils ant-utils checks log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

; checks plugin mdsd, web-module-type, tests
(defn get-environments []
  (list-files (param :package-environment-dir)))

(defn manifest [dir additional-entries]
  (ant-manifest {:file (str dir "/MANIFEST.MF")}
                (merge {"Implementation-Version" (param :version)} additional-entries)))

(defn package-jar
  ([dir jar-type]
    (package-jar dir jar-type "jar" {}))
  ([dir jar-type archive-type additional-manifest-entries]
    (log :debug  "packaging jar" dir " " jar-type " " archive-type)
    (manifest dir additional-manifest-entries)
    (ant-jar {:destFile (str (param "${dist-dir}/${module}") jar-type "." archive-type) :manifest (str dir "/MANIFEST.MF")}
             (ant-fileset {:dir dir}))))

(defn package-war [dir environment additional-manifest-entries]
  (log :debug  "packaging war" dir " " environment)
  ; TODO generate environment specific configuration
  ; generate environment specific war file
  (ant-war {:destFile (str dir "/" environment "/" (param :context-name (param :module)) ".war" )}
           (ant-zipfileset {:dir (param :dist-dir) :includes (param "${dist-dir}/${module}.jar") :prefix "WEB-INF/lib"})
           (ant-zipfileset {:dir (param :lib-runtime-dir) :prefix "WEB-INF/lib"})
           (when (has-plugin? "aspectj")
             (ant-zipfileset {:dir (param :lib-aspect-dir) :prefix "WEB-INF/lib"}))
           (ant-fileset {:dir "WebContent"})
           (when (has-plugin? "mdsd")
             (ant-fileset {:dir (param "${mdsd-generation-dir}/WebContent")}))))

(defn package-ear [dir environment additional-manifest-entries]
  (log :debug "packaging ear" dir " " environment))

(defn package-data []
  (log :debug  "packaging data" (param :dist-dir))
  (ant-zip {:destFile (param "${dist-dir}/${module}.zip")}
           (ant-fileset {:dir (param :module-dir) :excludes (param "${dist-dir} ${build-dir} ${lib-dir}")})))

; TODO handle source jars
(defn package-jars []
  (package-jar (param :build-classes-dir) "" "jar" {})
  (when (unittest?)
    (package-jar (param :build-unittest-classes-dir) "Unittest" "jar" {}))
  (when (integrationtest?)
    (package-jar (param :build-integrationtest-classes-dir) "Integrationtest" "jar" {}))
  (when (acceptancetest?)
    (package-jar (param :build-acceptancetest-classes-dir) "Acceptancetest" "jar" {}))
  (when (seq (param :package-additional-jars))
    (doseq [[dir archive-type] (param :package-additional-jars)]
      (package-jar dir archive-type)))
  )

(defn package-package
  "package package"
  []
  (when (code-module?)
    (package-jars))
  (when (web-module?)
    ; FIXME iterate over environments instead of using hardcoded "dev"
    (package-war (param :dist-dir) "dev" {}))
  (when (app-module?)
    ; TODO package app with start scripts
    )
  (when (console-module?)
    ; TODO package console app with start scripts
    )
  (when (data-module?)
    (package-data)))

(def package-config
  {:params [[:package-environment-dir "${module-dir}/env"]
            [:package-additional-jars []]]
   :functions [[:package package-package]]})

(defn plugin-init []
  (log :info  "initializing plugin package")
  (register-vars (:params package-config))
  (register-fns (:functions package-config)))
