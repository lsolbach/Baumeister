(ns org.soulspace.build.baumeister.plugins.clojure
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [join]]
        [org.soulspace.clj file file-search function string ]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]
        [org.soulspace.build.baumeister.config.registry]))

(defn remove-clj-ext [clj-path]
  (if (ends-with ".clj" clj-path)
    (substring 0 (- (str-length clj-path) 4) clj-path)
    clj-path))

(defn collect-namespaces [src-path]
  (map file-to-ns (map remove-clj-ext (map (partial relative-path src-path) (existing-files-on-path "clj" src-path)))))

(defn compile-clojure [dest-dir src-path class-path]
  (log :debug "compile-class-path" class-path)
  (ant-java {:classname "clojure.lang.Compile"
             :fork "true"
             :failonerror "true"
             :classpath class-path}
            (ant-variable {:key "clojure.compile.path" :value dest-dir})
            {:line (join " " (collect-namespaces src-path))}
            ))

(defn clojure-clean
  "clojure clean"
  []
  (delete-file (as-file (param :lib-runtime-dir)))
  (delete-file (as-file (param :lib-dev-dir))))

(defn clojure-init
  "clojure init"
  []
  (create-dir (as-file (param :lib-runtime-dir)))
  (create-dir (as-file (param :lib-dev-dir))))

(defn clojure-compile
  "clojure compile"
  []
  ; compute classpaths before compilation after dependencies have been initialized
  (compile-clojure (param :build-classes-dir) (param :clojure-source-path)
                   (class-path [(param :build-classes-dir) (param :clojure-source-path)
                                (jar-path (param :clojure-lib-path))]))
  (when (unittest?)
    (compile-clojure
      (param :build-unittest-classes-dir) (param :clojure-unittest-source-path)
      (class-path [(param :build-unittest-classes-dir) (param :clojure-unittest-source-path)
                   (param :build-classes-dir) (jar-path (param :clojure-unittest-lib-path))])))
  (when (integrationtest?)
    (compile-clojure
      (param :build-integrationtest-classes-dir) (param :clojure-integrationtest-source-path)
      (class-path [(param :build-integrationtest-classes-dir) (param :clojure-integrationtest-source-path)
                   (param :build-classes-dir) (jar-path (param :clojure-integrationtest-lib-path))])))
  (when (acceptancetest?)
    (compile-clojure
      (param :build-acceptancetest-classes-dir) (param :clojure-acceptancetest-source-path)
      (class-path [(param :build-acceptancetest-classes-dir) (param :clojure-acceptancetest-source-path)
                   (param :build-classes-dir) (jar-path (param :clojure-acceptancetest-lib-path))]))))

(defn register-source-paths []
  (if (has-plugin? "mdsd")
    (register-vars [[:clojure-source-path "src:${mdsd-generation-dir}/src"]
                    [:clojure-unittest-source-path "unittest:${mdsd-generation-dir}/unittest"]
                    [:clojure-integrationtest-source-path "integrationtest:${mdsd-generation-dir}/integrationtest"]
                    [:clojure-acceptancetest-source-path "acceptancetest:${mdsd-generation-dir}/acceptancetest"]])
    (register-vars [[:clojure-source-path "src"]
                    [:clojure-unittest-source-path "unittest"]
                    [:clojure-integrationtest-source-path "integrationtest"]
                    [:clojure-acceptancetest-source-path "acceptancetest"]])))

(defn plugin-init []
  (log :info "initializing plugin clojure")
  (register-source-paths)
  (register-vars [[:lib-runtime-dir "${lib-dir}/runtime"]
                  [:lib-dev-dir "${lib-dir}/dev"]])
  (register-vars [[:clojure-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
                  [:clojure-unittest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
                  [:clojure-integrationtest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
                  [:clojure-acceptancetest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]])
  (register-fns [[:clean clojure-clean]
                 [:init clojure-init]
                 [:compile clojure-compile]]))
