(ns org.soulspace.build.baumeister.utils.cli
  (:use [org.soulspace.clj string file]))

; TODO move to application framework?
(defn option? [arg]
  "Tests if the string is an option, which starts with the character '-'."
  (starts-with "-" arg))

(defn parse-options [spec options]
  "Parses a sequence of options."
  
  )

(defn parse-args [args]
  "Parses the args sequence into a vector of options and arguments."
  [(filter option? args) (filter (complement option?) args)])



