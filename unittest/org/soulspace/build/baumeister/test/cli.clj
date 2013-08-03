(ns org.soulspace.build.baumeister.test.cli
  (:use [clojure.test]
        [org.soulspace.build.baumeister.utils.cli]))

(def opt-def1 [["--define" "-D" "Define a variable"]
               ["--help" "-h" "Display help"]
               ["--file" "-f" "File to use" :default "module.clj"]])

(def opt-def2 [["--define" "-D" "Define a variable" :parse-fn #(apply hash-map (clojure.string/split % #"="))]
               ["--help" "-h" "Display help"]
               ["--file" "-f" "File to use" :default "module.clj"]])

(deftest matches-option?-test
  (is (true? (matches-option? "-Dx=y" {:short "-D"})))
  (is (false? (matches-option? "Dx=y" {:short "-D"})))
  (is (false? (matches-option? "--Dx=y" {:short "-D"})))
  (is (false? (matches-option? "-Ex=y" {:short "-D"})))
  )

(deftest build-specs-test
  )

;(run-tests)