(ns org.soulspace.build.baumeister.plugins.cobertura
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [join split]]
        [org.soulspace.clj file]
        [org.soulspace.build.baumeister.utils ant-utils files log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

(def cobertura-classpath (lib-path ["cobertura" "asm" "asm-tree" "jakarta-oro"])) ; log4j?
(ant-taskdef {:classpath cobertura-classpath :resource "tasks.properties"})
(define-ant-task ant-cobertura-instrument cobertura-instrument)
(define-ant-task ant-cobertura-report cobertura-report)
(define-ant-type ant-ignore net.sourceforge.cobertura.ant.Ignore)

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/cobertura-instrument
                       net.sourceforge.cobertura.ant.Ignore]
  [_ task regex] (doto (.createIgnore task) (.setRegex regex)))

(defn instrument-task []
  (ant-cobertura-instrument {:todir (param :build.instrumented.dir)
                             :datafile (param :cobertura-data-file)}
                            (ant-ignore {:regex "org.apache.log4j.*"})
                            (ant-ignore {:regex "antlr.*"})
                            (ant-fileset {:dir (param :build-classes-dir)
                                          :includes "**/*.class" :excludes "**/*Test.class"})))

(defn report-task []
  (ant-cobertura-report {:destdir (param :cobertura-report-dir) :format "xml"
                         :datafile (param :cobertura-data-file)}
                        (ant-fileset {:dir (param :module-dir) :includes (join " " (split (source-path) #":"))})))

(def cobertura-run-classpath
  (class-path [(param :build-cobertura-dir) cobertura-classpath])) ; add unittest classpath

(defn cobertura-junit [class-path test-dir report-dir]
  (log :debug class-path test-dir)
  (ant-junit {:fork (param :junit-fork) :forkMode (param :junit-fork-mode)
              :maxmemory (param :junit-max-memory)
              :printsummary (param :junit-print-summary)
              :errorProperty "unittest.error"
              :failureProperty "unittest.error"}
             (ant-path class-path)
             (ant-variable {:key "base.dir" :value (param :module-dir)})
             (ant-variable {:key "net.sourceforge.cobertura.datafile" :value (param :cobertura-data-file)})
             (ant-formatter {:type "brief" :useFile "false"})
             {:todir report-dir
              :fileset (ant-fileset {:dir test-dir :includes "**/*Test.class" :excludes "junit/**/*Test.class,**/Abstract*.class"})}))

(defn cobertura-clean []
  (log :info "cleaning cobertura...")
    (delete-file (as-file (param :build-cobertura-dir)))
    (delete-file (as-file (param :cobertura-report-dir))))

(defn cobertura-init []
  (log :info "initializing cobertura...")
  (create-dir (as-file (param :cobertura-report-dir)))
  (create-dir (as-file (param :build-cobertura-dir))))

(defn cobertura-pre-coverage []
  (log :info "pre-coverage cobertura...")
  (instrument-task))

(defn cobertura-coverage []
  (log :info "coverage cobertura...")
  (cobertura-junit cobertura-run-classpath (param :build-unittest-classes-dir) (param :unittest-unittest-report-dir)))

(defn cobertura-post-coverage []
  (log :info "post-coverage cobertura...")
  (report-task))

(def cobertura-config
  {:params [[:build-cobertura-dir "${build-dir}/cobertura"]
            [:cobertura-data-file "${build-cobertura-dir}/cobertura.ser"]
            [:cobertura-report-dir "${build-report-dir}/cobertura"]]
   :functions [[:clean cobertura-clean]
               [:init cobertura-init]
               [:pre-coverage cobertura-pre-coverage]
               [:coverage cobertura-coverage]
               [:post-coverage cobertura-post-coverage]]
   :dependencies [[["net.sourceforge.cobertura" "cobertura" "1.9.4.1"]]
                  [["asm" "asm" "3.3.1"]]
                  [["asm" "asm-tree" "3.3.1"]]
                  [["oro" "oro" "2.0.8"]]]})

(defn plugin-init []
  (log :info "initializing plugin cobertura")
  (register-vars (:params cobertura-config))
  (register-fns (:functions cobertura-config)))
