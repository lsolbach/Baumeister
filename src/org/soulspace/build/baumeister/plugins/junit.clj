(ns org.soulspace.build.baumeister.plugins.junit
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file]
        [org.soulspace.clj.java type-conversion]
        [org.soulspace.build.baumeister.utils ant-utils files checks log message]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

(defn junit [class-path test-dir report-dir]
  (log :debug "calling junit with parameters" class-path test-dir report-dir)
  (ant-junit {:fork (param :junit-fork) :forkMode (param :junit-fork-mode)
              :maxmemory (param :junit-max-memory)
              :printsummary (param :junit-print-summary)
              :errorProperty "junit.error"
              :failureProperty "junit.error"}
             (ant-path class-path)
             (ant-variable {:key "base.dir" :value (param :module-dir)})
             (ant-formatter {:type "brief" :useFile "false"})
             (ant-formatter {:type "xml"})
             {:todir report-dir
              :fileset (ant-fileset {:dir test-dir :includes "**/*Test.class" :excludes "junit/**/*Test.class,**/Abstract*.class"})}))

(defn junit-clean
  "junit clean"
  []
  (message :fine "cleaning junit...")
  (delete-file (as-file (param :report-acceptancetest-dir)))
  (delete-file (as-file (param :report-integrationtest-dir)))
  (delete-file (as-file (param :report-unittest-dir))))

(defn junit-init
  "junit init"
  []
  (message :fine "initializing junit...")
  (create-dir (as-file (param :report-unittest-dir)))
  (create-dir (as-file (param :report-integrationtest-dir)))
  (create-dir (as-file (param :report-acceptancetest-dir))))

(defn junit-unittest
  "Run JUnit unit tests."
  []
  (message :fine "running junit unit tests...")
  (let [unittest-classpath (class-path [(param :build-unittest-classes-dir) (param :build-classes-dir)
                                         (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))
                                         (jar-path (param :lib-aspect-dir))])]
    (junit unittest-classpath (param :build-unittest-classes-dir) (param :report-unittest-dir))))

(defn junit-integrationtest
  "Run JUnit integration tests."
  []
  (message :fine "running junit integration tests...")
  (let [integrationtest-classpath (class-path [(param :build-integrationtest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))
                                  (jar-path (param :lib-aspect-dir))])]
    (junit integrationtest-classpath (param :build-integrationtest-classes-dir) (param :report-integrationtest-dir))))

(defn junit-acceptancetest
  "Run JUnit acceptance tests."
  []
  (message :fine "running junit acceptance tests...")
  (let [acceptancetest-classpath (class-path [(param :build-acceptancetest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))
                                  (jar-path (param :lib-aspect-dir))])]
    (junit acceptancetest-classpath (param :build-acceptancetest-classes-dir) (param :report-acceptancetest-dir))))

(def config 
  {:params [[:report-unittest-dir "${build-report-dir}/unittest"]
            [:report-integrationtest-dir "${build-report-dir}/integrationtest"]
            [:report-acceptancetest-dir "${build-report-dir}/acceptancetest"]
            [:junit-fork "true"]
            [:junit-fork-mode "once"]
            [:junit-max-memory "512m"]
            [:junit-print-summary "false"]]
   :functions [[:clean junit-clean]
               [:init junit-init]
               [:unittest junit-unittest]
               [:integrationtest junit-integrationtest]
               [:acceptancetest junit-acceptancetest]]})
