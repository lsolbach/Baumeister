(ns org.soulspace.build.baumeister.plugins.clojuretest
  (:use [clojure.test]
        [clojure.test.junit]
        [clojure.string :only [split]]
        [clojure.java.io :only [as-file as-url writer]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.utils classpath files log message]
        [org.soulspace.build.baumeister.config registry]))

(defn find-namespaces
  "Find all clojure namespaces in a given directory."
  [dir]
  (let [dir (as-file dir)]
    (if (is-dir? dir)
      (->>
        (all-files-by-extension "clj" dir)
        (map #(second (re-matches (re-pattern (str (absolute-path dir) "/(.*)\\.clj")) (absolute-path %))))
        (map file-to-ns)
        (map symbol)))))

(defn perform-test
  "Perform tests for the namespace and write the result to the report directory."
  [report-dir nspace]
  (message :normal "Testing namspace" nspace)
  (require nspace)
  (with-open [writer (writer (str report-dir "/TEST-" (name nspace) ".xml"))]
    (binding [*test-out* writer]
      (with-junit-output
          (message :normal (run-tests nspace))))))

(defn perform-tests
  "Perform tests."
  [source-key class-dir report-dir]
  (when-let [source-dirs (source-dirs source-key)]
    (register-classpath-entries [class-dir])
    (let [namespaces (flatten (map find-namespaces source-dirs))]
      (doseq [nspace namespaces]
        (perform-test report-dir nspace)))))

(defn clojuretest-clean
  "clojuretest clean"
  []
  (message :fine "cleaning test report dirs...")
  (delete-file (as-file (param :report-acceptancetest-dir)))
  (delete-file (as-file (param :report-integrationtest-dir)))
  (delete-file (as-file (param :report-unittest-dir))))

(defn clojuretest-init
  "clojuretest init"
  []
  (message :fine "initializing test report dirs...")
  (create-dir (as-file (param :report-unittest-dir)))
  (create-dir (as-file (param :report-integrationtest-dir)))
  (create-dir (as-file (param :report-acceptancetest-dir)))
  )

(defn clojuretest-unittest
  []
  (perform-tests :clojure-source-unittest-dir (param :build-unittest-classes-dir) (param :report-unittest-dir)))

(defn clojuretest-integrationtest
  []
  (perform-tests :clojure-source-integrationtest-dir (param :build-integrationtest-classes-dir) (param :report-integrationtest-dir)))

(defn clojuretest-acceptancetest
  []
  (perform-tests :clojure-source-acceptancetest-dir (param :build-acceptancetest-classes-dir) (param :report-acceptancetest-dir)))

(def config
  {:params [[:report-unittest-dir "${build-report-dir}/unittest"]
            [:report-integrationtest-dir "${build-report-dir}/integrationtest"]
            [:report-acceptancetest-dir "${build-report-dir}/acceptancetest"]]
   :functions [[:clean clojuretest-clean]
               [:init clojuretest-init]
               [:unittest clojuretest-unittest]
               [:integrationtest clojuretest-integrationtest]
               [:acceptancetest clojuretest-acceptancetest]
               ]})
