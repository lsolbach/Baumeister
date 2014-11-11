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
(def log-levels
  "Definition of the log levels."
  {:none 0 :fatal 10 :error 20 :warn 30 :info 40 :debug 50 :trace 60})
(def message-levels
  "Definition of the message levels."
  {:none 0 :very-important 10 :important 20 :info 30 :fine 40 :finer 50 :finest 60})

(def ^{:dynamic true :private true} current-log-file "log.txt")
(def ^{:dynamic true :private true} current-log-level (:error log-levels))
(def ^{:dynamic true :private true} current-msg-level (:info message-levels))

(defn set-log-level
  "Sets the current log level to level."
  [level]
  (def current-log-level (get log-levels (keyword level) (log-levels :warn))))

(defn set-message-level
  "Sets the current message level to level."
  [level]
  (def current-msg-level (get message-levels (keyword level) (message-levels :info))))

(defn log
  "Log a message with the specified level."
  [level & msgs]
  (let [msg-lvl (get log-levels (keyword level) (log-levels :error))]
    (when (<= msg-lvl current-log-level)
      (println (keyword level) (join " " msgs)))))

(defn message 
  "Show a user visible message with the specified level."
  [level & msgs]
  (apply log level msgs) ; log the message too
  (let [msg-lvl (get message-levels (keyword level) (message-levels :info))]
    (when (<= msg-lvl current-msg-level)
      (println (join " " msgs)))))

; TODO helpful?
(defn thow-logged
  "Throws an exception after logging it's message."
  [level e]
  (log level (.getMessage e))
  (log :debug "\t" (join "\n\t" (map str (.getStacktrace e))))
  (throw e))

; convenience log functions
(def fatal (partial log :fatal))
(def error (partial log :error))
(def warn (partial log :warn))
(def info (partial log :info))
(def debug (partial log :debug))
(def trace (partial log :trace))
