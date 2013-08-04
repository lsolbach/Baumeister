(ns org.soulspace.build.baumeister.maven.metadata-model
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]))

;
; Maven metadata model 1.1.0
;

(defprotocol Metadata
  )

(defn metadata-versions
  [zipper]
  (zx/xml-> zipper :versioning :version))

