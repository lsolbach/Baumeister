;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.site.page
  (:require [org.soulspace.clj.xhtml.xhtml5-dsl :as xhtml5]))

(defn header
  []
  )

(defn menu
  []
  )

(defn footer
  []
  )

(defn page
  [path page-title content]
  (xhtml5/html {}
        (xhtml5/head {}
              (xhtml5/title {} page-title)
              (xhtml5/link {:rel "stylesheet" :type "text/css" :href "main.css"})) ; relative stylesheet path
    (header)
    (menu)
    (content)
    (footer)))