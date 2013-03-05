(ns org.soulspace.build.baumeister.test.artifact
  (:use [clojure.test] 
        [org.soulspace.build.baumeister.utils.artifact]))

(deftest match-identifier-true
  (is (true? (identifier-match? nil "module")))
  (is (true? (identifier-match? "" "module")))
  (is (true? (identifier-match? "module" "module"))))

(deftest match-identifier-false
  (is (false? (identifier-match? "module1" "module"))))

(deftest match-type-true
  (is (true? (type-match? nil "jar")))
  (is (true? (type-match? "" "jar")))
  (is (true? (type-match? "jar" "jar"))))

(deftest match-type-false
  (is (false? (type-match? "zip" "jar"))))

; TODO enhance tests for artifact matching
(deftest match-artifact-true
  (is (true? (artifact-match? (new-artifact-pattern "" "" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0" "module" "jar"))))
  )

(deftest match-artifact-false
  (is (false? (artifact-match? (new-artifact-pattern "org.apache" "" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "" "module1" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "" "" "1.1.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.apache" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.soulspace" "module1" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.1.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  )

(run-tests)
