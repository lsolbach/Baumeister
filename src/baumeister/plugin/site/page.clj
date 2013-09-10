(ns baumeister.plugin.site.page
  (:use [org.soulspace.xhtml.xhtml5-dsl]))

(defn page
  [path page-title content]
  (html {}
        (head {}
              (title {} page-title)
              (link {:rel "stylesheet" :type "text/css" :href "main.css"})) ; relative stylesheet path
    (header)
    (menu)
    (content)
    (footer)))

