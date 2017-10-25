(defproject org.soulspace.baumeister/MDDArchitecturePlugin "0.7.0"
  :description "MDDArchitecture plugin for generating artifacts from UML/XMI models with the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.soulspace.clj/CljModelGenerator "0.5.5"]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
