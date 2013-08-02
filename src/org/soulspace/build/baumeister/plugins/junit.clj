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
  (delete-file (as-file (param :acceptancetest-report-dir)))
  (delete-file (as-file (param :integrationtest-report-dir)))
  (delete-file (as-file (param :unittest-report-dir))))

(defn junit-init
  "junit init"
  []
  (message :fine "initializing junit...")
  (create-dir (as-file (param :unittest-report-dir)))
  (create-dir (as-file (param :integrationtest-report-dir)))
  (create-dir (as-file (param :acceptancetest-report-dir))))

(defn junit-unittest
  "Run JUnit unit tests."
  []
  (message :fine "running junit unit tests...")
  (junit (param :unittest-class-path) (param :build-unittest-classes-dir) (param :unittest-report-dir)))

(defn junit-integrationtest
  "Run JUnit integration tests."
  []
  (message :fine "running junit integration tests...")
  (junit (param :integrationtest-class-path) (param :build-integrationtest-classes-dir) (param :integrationtest-report-dir)))

(defn junit-acceptancetest
  "Run JUnit acceptance tests."
  []
  (message :fine "running junit acceptance tests...")
  (junit (param :acceptancetest-class-path) (param :build-acceptancetest-classes-dir) (param :acceptancetest-report-dir)))

(defn register-paths []
  ; FIXME do not build classpaths at plugin initialization time but at junit call time
  ; FIXME otherwise new dependencies won't get picked up (see java plugin)
  (if (has-plugin? "aspectj")
    (register-vars [[:unittest-class-path
                     (class-path [(param :build-unittest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))
                                  (jar-path (param :lib-aspect-dir))])]
                    [:integrationtest-class-path
                     (class-path [(param :build-integrationtest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))
                                  (jar-path (param :lib-aspect-dir))])]
                    [:acceptancetest-class-path
                     (class-path [(param :build-acceptancetest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))
                                  (jar-path (param :lib-aspect-dir))])]])
    (register-vars [[:unittest-class-path
                     (class-path [(param :build-unittest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))])]
                    [:integrationtest-class-path
                     (class-path [(param :build-integrationtest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))])]
                    [:acceptancetest-class-path
                     (class-path [(param :build-acceptancetest-classes-dir) (param :build-classes-dir)
                                  (jar-path (param :lib-runtime-dir)) (jar-path (param :lib-dev-dir))])]])))

(def config 
  {:params [[:unittest-report-dir "${build-report-dir}/junit/unittest"]
            [:integrationtest-report-dir "${build-report-dir}/junit/integrationtest"]
            [:acceptancetest-report-dir "${build-report-dir}/junit/acceptancetest"]
            [:junit-fork "true"]
            [:junit-fork-mode "once"]
            [:junit-max-memory "512m"]
            [:junit-print-summary "false"]]
   :functions [[:clean junit-clean]
               [:init junit-init]
               [:unittest junit-unittest]
               [:integrationtest junit-integrationtest]
               [:acceptancetest junit-acceptancetest]]})

(defn plugin-init []
  (log :info  "initializing plugin junit")
  (register-vars (:params config))
  (register-paths)
  (register-fns (:functions config)))
