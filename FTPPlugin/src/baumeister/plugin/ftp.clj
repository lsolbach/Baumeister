;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.ftp
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj.net.ftp ftp-client]))

(defn ftp-upload
  "Upload files"
  ([server local remote]
    (with-ftp-connection server
      (store-file remote (input-stream local))))
  ([server file-names]
    (with-ftp-connection server
      (doseq [file-name file-names]
        (store-file file-name (input-stream file-name))))))

(def config
  {:params []
   :steps []
   :functions []})