;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.utils.log
  (:use [clojure.string :only [join]]
        [baumeister.config registry]))

; TODO finer grained log levels?
(def log-levels {:fatal 0 :error 1 :warn 2 :info 3 :debug 4 :trace 5})
(def ^{:dynamic true :private true} current-log-level (:error log-levels))

(defn set-log-level
  "Sets the current log level to level."
  [level]
  (def current-log-level (get log-levels (keyword level) 2)))

(defn log [level & msgs]
  (let [msg-lvl (get log-levels (keyword level) 3)]
    (when (<= msg-lvl current-log-level)
      (println (keyword level) (join " " msgs)))))