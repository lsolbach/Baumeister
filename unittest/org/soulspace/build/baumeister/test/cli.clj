;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
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