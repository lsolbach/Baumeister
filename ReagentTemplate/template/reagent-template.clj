[
 {:type :directory
  :name "dev/clj"}
 {:type :directory
  :name "src/clj"}
 {:type :directory
  :name "src/cljs"}
 {:type :directory
  :name "src/cljc"}
 {:type :directory
  :name "src/scss"}
 {:type :directory
  :name "test/clj"}
 {:type :directory
  :name "test/cljc"}
 {:type :directory
  :name "resources/public/css"}
 {:type :file
  :name "module.clj"
  :content "[:module \"${module}\"
 :project \"${project}\"
 :type :${type}
 :version \"0.1.0\"
 :description \"\"
 :plugins [[\"org.soulspace.baumeister/ClojurePlugin\"]
           [\"org.soulspace.baumeister/ClojureScriptPlugin\"]
           [\"org.soulspace.baumeister/PackagePlugin\"]]
 :dependencies [[\"org.clojure/clojure, 1.8.0\"]
                [\"org.clojure/clojurescript, 1.9.89\"]
                [\"com.cognitect/clojurescript, 1.9.89\"]
                [\"org.clojure/clojurescript, 1.9.89\"]
                ]
]"}
 {:type :file
  :name "README.md"
  :content "${module}
==========

${module} reagent web project

Copyright
---------

License
-------

"}
 {:type :file
  :name ".gitignore"
  :content "/bin/
/build/"}
 {:type :file
  :name "src/clj/${module}/server.clj"
  :content "(ns ${module}.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defroutes routes
  (GET \"/\" _
    {:status 200
     :headers {\"Content-Type\" \"text/html; charset=utf-8\"}
     :body (io/input-stream (io/resource \"public/index.html\"))})
  (resources \"/\"))

(def http-handler
  (-> routes
      (wrap-defaults api-defaults)
      wrap-with-logger
      wrap-gzip))

(defonce server (atom nil))

(defn restart-server [& [port]]
  (swap! server (fn [value]
                  (when value (.stop value))
                  (run-jetty #'http-handler {:port (or port 10555) :join? false}))))

(defn -main [& [port]]
  (let [port (some-> (or port (env :port)) (Integer.))]
    (restart-server port)))
"}
 {:type :file
  :name "src/cljs/${module}/core.cljs"
  :content "(ns ${module}.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(defonce app-state (r/atom {:greeting \"Hello World!\"}))

(defn greeting []
  [:h1 (:greeting @app-state)])

(defn page
  []
  [:div
   [greeting]])

(r/render [page] (js/document.getElementById \"app\"))
"}
 {:type :file
  :name "src/cljs/${module}/common.cljc"
  :content "(ns ${module}.common)

(defn shared-fn
  \"A function that is shared between clj and cljs\"
  []
  (println \"cljc!\"))
"}
 {:type :file
  :name "dev/clj/${module}/user.clj"
  :content "(ns user
  (:require [${module}.server :refer [restart-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]))

;; Let Clojure warn you when it needs to reflect on types, or when it does math
;; on unboxed numbers. In both cases you should add type annotations to prevent
;; degraded performance.
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(def http-handler
  (wrap-reload #'annuity-web.server/http-handler))

(defn go []
  (figwheel/start-figwheel!)
  (restart-server)
  (prn \"Application restartet.\"))

(def run restart-server)

(def browser-repl figwheel/cljs-repl)
"}
 {:type :file
  :name "src/scss/style.css"
  :content "@charset \"UTF-8\";

h1 {
  text-decoration: underline;
}

"}
 {:type :file
  :name "resources/public/index.html"
  :content "<!DOCTYPE html>
<html>
  <head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
    <link href=\"css/style.css\" rel=\"stylesheet\" type=\"text/css\">
  </head>
  <body>
    <div id=\"app\"></div>
    <script src=\"js/compiled/reagent_test.js\" type=\"text/javascript\"></script>
  </body>
</html>

"}
 {:type :file
  :name "resources/public/css/style.css"
  :content "
h1 {
  text-decoration: underline;
}

"}


 ]