;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.global
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file system net]
        [baumeister.utils ant-utils log]
        [baumeister.config registry plugin-registry]))

(defn init-proxies
  "Initializes HTTP(S) Proxies."
  []
  (let [http-proxy-bybasshosts (param :http-proxy-bybasshosts)
        http-proxy-host (param :http-proxy-host)
        http-proxy-port (str (param :http-proxy-port 3128))
        https-proxy-host (param :https-proxy-host)
        https-proxy-port (str (param :https-proxy-port 3128))]
    (when http-proxy-host
      (set-system-property "java.net.useSystemProxies" "true")
      (if (seq http-proxy-bybasshosts)
        (set-http-proxy http-proxy-host http-proxy-port http-proxy-bybasshosts)
        (set-http-proxy http-proxy-host http-proxy-port))
      (if (seq https-proxy-host)
        (set-https-proxy https-proxy-host https-proxy-port)))))
      ;(println (into {} (System/getProperties)))


(defn global-clean
  "Global clean"
  []
  (log :info "cleaning globally...")
  (delete-dir (as-file (param :build-dir))))

(defn global-init
  "Global initialization"
  []
  (log :info "initializing globally...")
  (init-proxies)
  (create-dir (as-file (param :build-dir))))

(def config
  {:params [[:module-dir "."]
            [:build-dir "${module-dir}/build"]
            [:lib-dir "${build-dir}/lib"]
            [:dist-dir "${build-dir}/dist"]
            [:build-classes-dir "${build-dir}/classes"]
            [:build-unittest-classes-dir "${build-dir}/unittest/classes"]
            [:build-integrationtest-classes-dir "${build-dir}/integrationtest/classes"]
            [:build-acceptancetest-classes-dir "${build-dir}/acceptancetest/classes"]
            [:build-report-dir "${build-dir}/report"]]
   :steps [[:clean global-clean]
           [:init global-init]]
   :functions []})
