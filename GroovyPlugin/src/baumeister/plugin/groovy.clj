(ns baumeister.plugin.groovy
  (:use [baumeister.utils files ant-utils checks log]
        [baumeister.config registry]))

(ant-taskdef {:name "groovyc"
              :classname "org.codehaus.groovy.ant.Groovyc"})
(define-ant-task ant-groovyc groovyc)

(defn compile-groovy
  [destdir srcdir class-path]
  (ant-groovyc {:fork "true"
                :includeantruntime "false"
                :destdir destdir
                :encoding (param :groovy-source-encoding)
                :srcdir srcdir
                :classpath class-path}))

(defn groovy-compile
  []
  (when-let [source-dirs (seq (source-dirs :groovy-source-dir))]
    (compile-groovy (param :build-classes-dir) (dir-path source-dirs) (jar-path (param :groovy-lib-path))))
  (when-let [source-dirs (seq (source-dirs :groovy-source-unittest-dir))]
    (compile-groovy (param :build-unittest-classes-dir) (dir-path source-dirs)
                  (str (param :build-classes-dir) ":" (jar-path (param :groovy-unittest-lib-path)))))
  (when-let [source-dirs (seq (source-dirs :groovy-source-integrationtest-dir))]
    (compile-groovy (param :build-integrationtest-classes-dir) (dir-path source-dirs)
                  (str (param :build-classes-dir) ":" (jar-path (param :groovy-integrationtest-lib-path)))))
  (when-let [source-dirs (seq (source-dirs :groovy-source-acceptancetest-dir))]
    (compile-groovy (param :build-acceptancetest-classes-dir) (dir-path source-dirs)
                  (str (param :build-classes-dir) ":" (jar-path (param :groovy-acceptancetest-lib-path))))))

(def config
  {:params [[:groovy-source-encoding "${source-encoding}"]
            [:groovy-compiler-fork "${compiler-fork}"]
            [:groovy-compiler-maxmem "${compiler-maxmem}"]
            [:groovy-source-dir "${source-dir}"]
            [:groovy-source-unittest-dir "${source-unittest-dir}"]
            [:groovy-source-integrationtest-dir "${source-integrationtest-dir}"]
            [:groovy-source-acceptancetest-dir "${source-acceptancetest-dir}"]
            [:groovy-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:groovy-unittest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:groovy-integrationtest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:groovy-acceptancetest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]]
   :steps [:compile groovy-compile]
   :functions []})