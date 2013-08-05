;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
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