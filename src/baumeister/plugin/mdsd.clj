;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.mdsd
  (:use [clojure.string :only [split]]
        [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.clj.modelgenerator generator]
        [baumeister.utils ant-utils log]
        [baumeister.config registry plugin-registry]))

; TODO handle model dependencies from mdsd plugin?
; TODO at least access to the dependency tree is needed (to specify model dependencies in the right order)?
(defn profile-files [path profiles]
  (let [search-path (map as-file (split path #":"))
        xmi-locator (file-locator search-path "xmi")]
    (map #(xmi-locator %) profiles)))

(defn profile-finder [path]
  (existing-files-on-path "xmi" path))

; TODO find required profiles by a profile search path?
(defn std-profiles []
  (profile-files (param "${lib-generator-dir}/profiles") (param :mdsd-std-profiles)))

; TODO find required profiles by a profile search path?
(defn profiles []
  (if (param :mdsd-profiles) ; preserve order of profiles if specified (as long as the dependency order is not used)
    (profile-files (param "${lib-model-dir}") (param :mdsd-profiles))
    (profile-finder (param "${lib-model-dir}"))))

; TODO find required profiles by a profile search path?
(defn generation-context []
  {:model-type "design-model"
   :model (param "${mdsd-model-dir}/${mdsd-model-name}")
   :destDir (param :mdsd-generation-dir)
   :backupDir (param :mdsd-backup-dir)
   :templateDirs (param :mdsd-template-path)
;   :profiles (profile-files (param :mdsd-profile-path) (param :mdsd-std-profiles))})
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
  (let [gen-config (load-file (str (param "${mdsd-config-dir}/${mdsd-config-file}")))]
    (remove nil? (flatten (map #(% gen-config) (type-generator-mapping (param :type)))))))

(defn mdsd-clean []
  (delete-dir (as-file (param :mdsd-backup-dir)))
  (delete-dir (as-file (param :mdsd-generation-dir)))
  (delete-dir (as-file (param :lib-model-dir)))
  (delete-dir (as-file (param :lib-generator-dir))))

(defn mdsd-init []
  (create-dir (as-file (param :lib-generator-dir)))
  (create-dir (as-file (param :lib-model-dir)))
  (create-dir (as-file (param :mdsd-generation-dir)))
  (create-dir (as-file (param :mdsd-backup-dir))))

(defn mdsd-pre-generate []
  (log :info  "preparing generation from model...")
  (ant-delete {}
              (ant-fileset {:dir (param :mdsd-backup-dir)}))
  (ant-copy {:todir (param :mdsd-backup-dir) :overwrite "true"}
            (ant-fileset {:dir (param :mdsd-generation-dir)}))
  (ant-delete {} 
              (ant-fileset {:dir (param :mdsd-generation-dir)}))
  (ant-unzip {:src (param "${mdsd-model-dir}/${mdsd-model-name}.zargo")
              :dest (param "${mdsd-model-dir") :overwrite "true"}
             (ant-patternset {:includes (param "${mdsd-model-name}.xmi")})))

(defn mdsd-generate []
  (log :info  "generating artifacts from model...")
  (let [gen-ctx (generation-context)
        gens (generators)]
    (log :debug "Generation Context:" gen-ctx)
    (log :debug "Generators:" gens)
    (generate-all gen-ctx gens)))

(defn mdsd-post-generate []
  (log :info  "postprocessing generation from model..."))

(def config 
  {:params [[:lib-generator-dir "${lib-dir}/generator"]
            [:lib-model-dir "${lib-dir}/model"]
            [:mdsd-model-dir "${module-dir}/model"]
            [:mdsd-model-name "${module}"]
            [:mdsd-generation-dir "${generation-dir}"]
            [:mdsd-backup-dir "${build-dir}/backup"]
            [:mdsd-config-dir "${module-dir}/config"]
            [:mdsd-config-file "generators.clj"]
            [:mdsd-template-path "${lib-generator-dir}/templates"]
            [:mdsd-profile-path "${lib-generator-dir}/profiles:${lib-model-dir}"]
            [:mdsd-std-profiles ["argouml/default-uml14"]]
            [:mdsd-profiles nil]]
   :functions [[:clean mdsd-clean]
               [:init mdsd-init]
               [:pre-generate mdsd-pre-generate]
               [:generate mdsd-generate]
               [:generate mdsd-post-generate]]})
