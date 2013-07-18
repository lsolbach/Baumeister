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

(deftest compare-same
  (is (same-version? (new-version nil) nil))
  (is (same-version? (new-version "1") (new-version "1")))
  (is (same-version? (new-version "1.1") (new-version "1.1")))
  (is (same-version? (new-version "1.1.1") (new-version "1.1.1")))
  (is (same-version? (new-version "1.1.1rc1") (new-version "1.1.1rc1")))
  (is (same-version? (new-version "1.1.1pl2") (new-version "1.1.1pl2")))
  )

(deftest compare-greater
  (is (greater-version? (new-version "1") nil))
  (is (greater-version? (new-version "2") (new-version "1")))
  (is (greater-version? (new-version "2") (new-version "1.1")))
  (is (greater-version? (new-version "2.1") (new-version "1.1")))
  (is (greater-version? (new-version "12.1") (new-version "11.1")))
  (is (greater-version? (new-version "2.a") (new-version "1.a")))
  (is (greater-version? (new-version "2.b") (new-version "2.a")))
  (is (greater-version? (new-version "2.b") (new-version "2.a")))
  (is (greater-version? (new-version "2.2") (new-version "2.1.3")))
  (is (greater-version? (new-version "2.2-beta") (new-version "2.2-alpha")))
  (is (greater-version? (new-version "2.b") (new-version "2.a")))
  (is (greater-version? (new-version "10") (new-version "2")))
  (is (greater-version? (new-version "1.10") (new-version "1.2")))
  )

(deftest compare-lesser
  (is (lesser-version? (new-version nil) (new-version "1")))
  (is (lesser-version? (new-version "1") (new-version "2")))
  (is (lesser-version? (new-version "1.1") (new-version "2")))
  (is (lesser-version? (new-version "1.1") (new-version "2.1")))
  (is (lesser-version? (new-version "1.1") (new-version "1.2")))
  (is (lesser-version? (new-version "1.1") (new-version "1.1.1")))
  (is (lesser-version? (new-version "1.1-alpha") (new-version "1.1-beta")))
  (is (lesser-version? (new-version "2") (new-version "10")))  
  (is (lesser-version? (new-version "1.2") (new-version "1.10")))  
  )

(deftest match-version-true
  (is (true? (version-match? nil "1.0.0")))
  (is (true? (version-match? "" "1.0.0")))
  (is (true? (version-match? "1.0.0" "1.0.0"))))

(deftest match-version-false
  (is (false? (version-match? "1.1.0" "1.0.0"))))

;
;
;
(deftest create-new-version
  (is (not (nil? (new-version "1.3.0")))))

(run-tests)

