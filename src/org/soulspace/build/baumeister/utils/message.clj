(ns org.soulspace.build.baumeister.utils.message
  (:use [clojure.string :only [join]]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.utils log]))

(def message-levels 
  "Definition of the message levels."
  {:none 0 :very-important 10 :important 20 :normal 30 :fine 40 :finer 50 :finest 60})

; TODO log messages too?
(defn message 
  "Create a message with the specified level."
  [level & msgs]
  (let [msg-lvl (get message-levels (keyword level) 30)
        lvl (get message-levels (keyword (param :message-level)) 20)]
    (when (<= msg-lvl lvl)
      (println (join " " msgs)))))