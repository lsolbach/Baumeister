(ns org.soulspace.build.baumeister.test.dependency
    (:use [clojure.test] 
          [org.soulspace.build.baumeister.utils artifact dependency]))

;(create-dependency {:project "org.soulspace" :module "test" :version "2.0"} "runtime" "test")
;(create-dependency ["org.soulspace" "test" "2.0"] "runtime" "test")
;(create-dependency ["org.soulspace" "test" "2.0" "tests" "jar"] "dev" "test")
;(create-dependency "org.soulspace" "test" "2.0" "runtime" "test")

(deftest create-dependency-dispatch-test
  (is (= (create-dependency-dispatch {:project "org.soulspace" :module "test" :version "2.0"}) :artifact-map))
  (is (= (create-dependency-dispatch {:project "org.soulspace" :module "test" :version "2.0"} "runtime") :artifact-map))
  (is (= (create-dependency-dispatch {:project "org.soulspace" :module "test" :version "2.0"} "runtime" "test") :artifact-map))
  (is (= (create-dependency-dispatch ["org.soulspace" "test" "2.0"]) :artifact-seq))
  (is (= (create-dependency-dispatch ["org.soulspace" "test" "2.0"] "runtime") :artifact-seq))
  (is (= (create-dependency-dispatch ["org.soulspace" "test" "2.0"] "runtime" "test") :artifact-seq))
  (is (= (create-dependency-dispatch "org.soulspace" "test" "2.0") :artifact-flat))
  (is (= (create-dependency-dispatch "org.soulspace" "test" "2.0" "runtime") :artifact-flat))
  (is (= (create-dependency-dispatch "org.soulspace" "test" "2.0" "runtime" "test") :artifact-flat))
  (is (= (create-dependency-dispatch "org.soulspace" "test" "2.0" "tests" "runtime" "test") :artifact-flat)))

(run-tests)
