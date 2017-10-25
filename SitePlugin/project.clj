(defproject org.soulspace.baumeister/SitePlugin "0.7.0"
  :description "Site generation plugin for the generation of module and project web sites with the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.soulspace.clj/CljXHtmlLibrary "0.2.2"]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
