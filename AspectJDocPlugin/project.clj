(defproject org.soulspace.baumeister/AspectJDocPlugin "0.7.0"
  :description "AspectJDoc plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.aspectj/aspectjrt "1.8.7"]
                 [org.aspectj/aspectjtools "1.8.7" :exclusions [org.apache.ant/ant]]]
  :dev-dependencies [["org.soulspace.baumeister/Baumeister, 0.7.0"]]
  :test-paths ["unittest"])
