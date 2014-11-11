(ns baumeister.plugin.scala
  (:use [baumeister.utils files ant-utils checks log]
        [baumeister.config registry]))

(ant-taskdef {:resource "scala/tools/ant/antlib.xml"})
(define-ant-task ant-scalac scalac)

(defn compile-scala
  [destdir srcdir class-path]
  (ant-scalac {:destdir destdir
               :encoding (param :scala-source-encoding)
               :srcdir srcdir
               :classpath class-path}))

(defn scala-compile
[]
(when-let [source-dirs (seq (source-dirs :scala-source-dir))]
  (compile-scala (param :build-classes-dir) (dir-path source-dirs) (jar-path (param :scala-lib-path))))
(when-let [source-dirs (seq (source-dirs :scala-source-unittest-dir))]
  (compile-scala (param :build-unittest-classes-dir) (dir-path source-dirs)
                (str (param :build-classes-dir) ":" (jar-path (param :scala-unittest-lib-path)))))
(when-let [source-dirs (seq (source-dirs :scala-source-integrationtest-dir))]
  (compile-scala (param :build-integrationtest-classes-dir) (dir-path source-dirs)
                (str (param :build-classes-dir) ":" (jar-path (param :scala-integrationtest-lib-path)))))
(when-let [source-dirs (seq (source-dirs :scala-source-acceptancetest-dir))]
  (compile-scala (param :build-acceptancetest-classes-dir) (dir-path source-dirs)
                (str (param :build-classes-dir) ":" (jar-path (param :scala-acceptancetest-lib-path))))))

; TODO add additional scala compiler params
(def config
  {:params [[:scala-source-encoding "${source-encoding}"]
            [:scala-source-dir "${source-dir}"]
            [:scala-source-unittest-dir "${source-unittest-dir}"]
            [:scala-source-integrationtest-dir "${source-integrationtest-dir}"]
            [:scala-source-acceptancetest-dir "${source-acceptancetest-dir}"]
            [:scala-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:scala-unittest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:scala-integrationtest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:scala-acceptancetest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]]
   :steps [:compile scala-compile]
   :functions []})
