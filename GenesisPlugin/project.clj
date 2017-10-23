(defproject org.soulspace.baumeister/ClojurePlugin "0.7.0"
  :description "Plugin for the creation of new modules with the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
;                 ["org.soulspace.baumeister/AspectJTemplate, 0.1.0, AspectJTemplate, zip" :data]
;                 ["org.soulspace.baumeister/BaumeisterPluginTemplate, 0.1.0, BaumeisterPluginTemplate, zip" :data]
;                 ["org.soulspace.baumeister/BaumeisterTemplateTemplate, 0.1.0, BaumeisterTemplateTemplate, zip" :data]
;                 ["org.soulspace.baumeister/ClojureTemplate, 0.1.0, ClojureTemplate, zip" :data]
;                 ["org.soulspace.baumeister/DataTemplate, 0.1.0, DataTemplate, zip" :data]
;                 ["org.soulspace.baumeister/JavaTemplate, 0.1.0, JavaTemplate, zip" :data]
                 ]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
