;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.mddgenerator
  (:require [clojure.string :as str])
  (:use [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file file-search function]
        [org.soulspace.clj.modelgenerator generator]
        [baumeister.utils ant-utils log]
        [baumeister.config registry]))

; TODO handle model dependencies from mdd plugin?
; TODO at least access to the dependency tree is needed (to specify model dependencies in the right order)?
(defn profile-files [path profiles]
  (let [search-path (map as-file (str/split path #":"))
        xmi-locator (file-locator search-path "xmi")]
    (map #(xmi-locator %) profiles)))

(defn profile-finder [path]
  (existing-files-on-path "xmi" path))

; TODO find required profiles by a profile search path?
(defn std-profiles []
  (profile-files (param "${lib-generator-dir}/profiles") (param :mddgenerator-std-profiles)))

; TODO find required profiles by a profile search path?
(defn profiles []
  (if (param :mddgenerator-profiles) ; preserve order of profiles if specified (as long as the dependency order is not used)
    (profile-files (param "${lib-model-dir}") (param :mddgenerator-profiles))
    (profile-finder (param "${lib-model-dir}"))))

; TODO find required profiles by a profile search path?
(defn generation-context []
  {:model-type "design-model"
   :model (param "${mddgenerator-model-dir}/${mddgenerator-model-name}")
   :destDir (param :mddgenerator-generation-dir)
   :backupDir (param :mddgenerator-backup-dir)
   :templateDirs (param :mddgenerator-template-path)
;   :profiles (profile-files (param :mddgenerator-profile-path) (param :mddgenerator-std-profiles))})
   :profiles (filter not-nil? (concat (std-profiles) (profiles)))})

(def type-generator-mapping 
  {:data [:doc-generators]
   :library [:doc-generators :standard-generators]
   :framework [:doc-generators :standard-generators]
   :component [:doc-generators :standard-generators]
   :application [:doc-generators :standard-generators :application-generators]
   :domain [:doc-generators :standard-generators :domain-generators]
   :integration [:doc-generators :standard-generators :integration-generators]
   :presentation [:doc-generators :standard-generators :presentation-generators]
   :webservice [:doc-generators :standard-generators :web-service-generators]
   :webfrontend [:doc-generators :standard-generators :web-frontend-generators]
   :consolefrontend [:doc-generators :standard-generators]
   :appfrontend [:doc-generators :standard-generators]})

(defn generators []
  (let [gen-config (load-file (str (param "${mddgenerator-config-dir}/${mddgenerator-config-file}")))]
    (remove nil? (flatten (map #(% gen-config) (type-generator-mapping (param :type)))))))

(defn mddgenerator-clean []
  (delete-dir (as-file (param :mddgenerator-backup-dir)))
  (delete-dir (as-file (param :mddgenerator-generation-dir)))
  (delete-dir (as-file (param :lib-model-dir)))
  (delete-dir (as-file (param :lib-generator-dir))))

(defn mddgenerator-init []
  (create-dir (as-file (param :lib-generator-dir)))
  (create-dir (as-file (param :lib-model-dir)))
  (create-dir (as-file (param :mddgenerator-generation-dir)))
  (create-dir (as-file (param :mddgenerator-backup-dir))))

; TODO replace ant usage (delete, copy, ...) where feasible 
(defn mddgenerator-pre-generate []
  (log :info  "preparing generation from model...")
  (ant-delete {}
              (ant-fileset {:dir (param :mddgenerator-backup-dir)}))
  (ant-copy {:todir (param :mddgenerator-backup-dir) :overwrite "true"}
            (ant-fileset {:dir (param :mddgenerator-generation-dir)}))
  (ant-delete {} 
              (ant-fileset {:dir (param :mddgenerator-generation-dir)}))
  (ant-unzip {:src (param "${mddgenerator-model-dir}/${mddgenerator-model-name}.zargo")
              :dest (param "${mddgenerator-model-dir") :overwrite "true"}
             (ant-patternset {:includes (param "${mddgenerator-model-name}.xmi")})))

(defn mddgenerator-generate []
  (log :info  "generating artifacts from model...")
  (let [gen-ctx (generation-context)
        gens (generators)]
    (log :debug "Generation Context:" gen-ctx)
    (log :debug "Generators:" (str (str/join ", " gens)))
    (generate-all gen-ctx gens)))

(defn mddgenerator-post-generate []
  (log :info  "postprocessing generation from model..."))

(def config 
  {:params [[:lib-generator-dir "${lib-dir}/generator"]
            [:lib-model-dir "${lib-dir}/model"]
            [:mddgenerator-model-dir "${module-dir}/model"]
            [:mddgenerator-model-name "${module}"]
            [:mddgenerator-generation-dir "${generation-dir}"]
            [:mddgenerator-backup-dir "${build-dir}/backup"]
            [:mddgenerator-config-dir "${module-dir}/config"]
            [:mddgenerator-config-file "generators.clj"]
            [:mddgenerator-template-path "${lib-generator-dir}/templates"]
            [:mddgenerator-profile-path "${lib-generator-dir}/profiles:${lib-model-dir}"]
            [:mddgenerator-std-profiles ["argouml/default-uml14"]]
            [:mddgenerator-profiles nil]]
   :steps [[:clean mddgenerator-clean]
           [:init mddgenerator-init]
           [:pre-generate mddgenerator-pre-generate]
           [:generate mddgenerator-generate]
           [:post-generate mddgenerator-post-generate]]
   :functions []})
