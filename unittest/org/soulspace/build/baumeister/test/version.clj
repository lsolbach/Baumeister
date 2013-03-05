(ns org.soulspace.build.baumeister.test.version
  (:use [clojure.test] 
        [org.soulspace.build.baumeister.utils.version]))

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

(deftest match-version-true
  (is (true? (version-match? nil "1.0.0")))
  (is (true? (version-match? "" "1.0.0")))
  (is (true? (version-match? "1.0.0" "1.0.0"))))

(deftest match-version-false
  (is (false? (version-match? "1.1.0" "1.0.0"))))

(run-tests)

