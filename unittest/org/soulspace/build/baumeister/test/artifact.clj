(ns org.soulspace.build.baumeister.test.artifact
  (:use [clojure.test] 
        [org.soulspace.build.baumeister.utils.artifact]))

(deftest numeric-true
  (is (numeric? "1"))
  (is (numeric? "10"))
  (is (numeric? "101"))
  )

(deftest numeric-false
  (is (not (numeric? nil)))
  (is (not (numeric? "10a")))
  (is (not (numeric? "abc")))
  (is (not (numeric? "ten")))
  )

(deftest compare-ver-equals
  (is (= (compare-revision nil nil) 0))
  (is (= (compare-revision "0" "0") 0))
  (is (= (compare-revision "1" "1") 0))
  (is (= (compare-revision "a" "a") 0))
  )

(deftest compare-ver-greater
  (is (> (compare-revision "1" nil) 0))
  (is (> (compare-revision "2" "1") 0))
  (is (> (compare-revision "10" "2") 0))
  )

(deftest compare-ver-less
  (is (< (compare-revision nil "1") 0))
  (is (< (compare-revision "1" "2") 0))
  (is (< (compare-revision "2" "10") 0))
  )

(deftest compare-version-equals
  (is (= (compare-version nil nil) 0))
  (is (= (compare-version "1" "1") 0))
  (is (= (compare-version "1.1" "1.1") 0))
  (is (= (compare-version "1.1.1" "1.1.1") 0))
  (is (= (compare-version "1.1.1rc1" "1.1.1rc1") 0))
  (is (= (compare-version "1.1.1pl2" "1.1.1pl2") 0))
  )

(deftest compare-version-greater
  (is (> (compare-version "1" nil) 0))
  (is (> (compare-version "2" "1") 0))
  (is (> (compare-version "2" "1.1") 0))
  (is (> (compare-version "2.1" "1.1") 0))
  (is (> (compare-version "12.1" "11.1") 0))
  (is (> (compare-version "2.a" "1.a") 0))
  (is (> (compare-version "2.b" "2.a") 0))
  (is (> (compare-version "2.b" "2.a") 0))
  (is (> (compare-version "2.2" "2.1.3") 0))
  (is (> (compare-version "2.2-beta" "2.2-alpha") 0))
  (is (> (compare-version "2.b" "2.a") 0))
  (is (> (compare-version "10" "2") 0))
  (is (> (compare-version "1.10" "1.2") 0))
  )

(deftest compare-version-less
  (is (< (compare-version nil "1") 0))
  (is (< (compare-version "1" "2") 0))
  (is (< (compare-version "1.1" "2") 0))
  (is (< (compare-version "1.1" "2.1") 0))
  (is (< (compare-version "1.1" "1.2") 0))
  (is (< (compare-version "1.1" "1.1.1") 0))
  (is (< (compare-version "1.1-alpha" "1.1-beta") 0))
  (is (< (compare-version "2" "10") 0))  
  (is (< (compare-version "1.2" "1.10") 0))  
  )

; TODO enhance tests for artifact matching
(deftest match-version-true
  (is (true? (version-match? nil "1.0.0")))
  (is (true? (version-match? "" "1.0.0")))
  (is (true? (version-match? "1.0.0" "1.0.0"))))

(deftest match-version-false
  (is (false? (version-match? "1.1.0" "1.0.0"))))

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

(deftest match-artifact-true
  (is (true? (artifact-match? (new-artifact-pattern "" "" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0" "runtime" "module" "jar"))))
  (is (true? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0" "runtime" "module" "jar" "Client"))))
  )

(deftest match-artifact-false
  (is (false? (artifact-match? (new-artifact-pattern "org.apache" "" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "" "module1" "") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "" "" "1.1.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.apache" "module" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.soulspace" "module1" "1.0.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.1.0") (new-artifact "org.soulspace" "module" "1.0.0"))))
  (is (false? (artifact-match? (new-artifact-pattern "org.soulspace" "module" "1.0.0" "dev") (new-artifact "org.soulspace" "module" "1.0.0"))))
  )

(run-tests)
