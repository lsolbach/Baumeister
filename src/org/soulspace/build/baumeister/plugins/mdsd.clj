(ns org.soulspace.build.baumeister.plugins.mdsd
  (:use [clojure.string :only [split]]
        [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.clj.modelgenerator generator]
        [org.soulspace.build.baumeister.utils ant-utils log]
        [org.soulspace.build.baumeister.config registry]))

; TODO handle model dependencies from mdsd plugin?
; TODO at least access to the dependency tree is needed (to specify model dependencies in the right order)

(defn profile-files [path profiles]
  (let [search-path (map as-file (split path #":"))
        xmi-locator (file-locator search-path "xmi")]
    (map #(xmi-locator %) profiles)))

(defn profile-finder [path]
  (existing-files-on-path "xmi" path))

(defn std-profiles []
  (profile-files (param "${lib-generator-dir}/profiles") (param :mdsd-std-profiles)))

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

(defn generators []
  (let [gen-config (load-file (str (param :mdsd-config-dir) "/mdsd_defaults.clj"))]
    (cond
      (= (param :type) "data")
      (flatten [(:doc-generators gen-config)])
      (= (param :type) "library")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config)])
      (= (param :type) "framework")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config)])
      (= (param :type) "application")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config) (:application-generators gen-config)])
      (= (param :type) "domain")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config) (:domain-generators gen-config)])
      (= (param :type) "integration")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config) (:integration-generators gen-config)])
      (= (param :type) "presentation")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config) (:presentation-generators gen-config)])
      (= (param :type) "webfrontend")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config) (:web-frontend-generators gen-config)])
      (= (param :type) "webservice")
      (flatten [(:doc-generators gen-config) (:standard-generators gen-config) (:web-service-generators gen-config)]))))

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
             (ant-fileset {:dir "." :includes (param "${mdsd-model-name}.xmi")})))

(defn mdsd-generate []
  (log :info  "generating artifacts from model...")
  (log :debug "Generation Context:" (generation-context))
  (log :debug "Generators:" (generators))
  (generate-all (generation-context) (generators)))

(defn mdsd-post-generate []
  (log :info  "postprocessing generation from model..."))

(defn plugin-init []
  (log :info  "initializing plugin mdsd")
  (register-vars [[:lib-generator-dir "${lib-dir}/generator"]
                  [:lib-model-dir "${lib-dir}/model"]
                  [:mdsd-model-dir "${module-dir}/model"]
                  [:mdsd-model-name "${name}"]
                  [:mdsd-generation-dir "${module-dir}/generated"]
                  [:mdsd-backup-dir "${module-dir}/backup"]
                  [:mdsd-config-dir "${lib-generator-dir}/config"]
                  [:mdsd-template-path "${lib-generator-dir}/std-templates2:${lib-generator-dir}/templates2"]
                  [:mdsd-profile-path "${lib-generator-dir}/profiles:${lib-model-dir}"]
                  [:mdsd-std-profiles ["argouml/default-uml14" "MDSDProfile"]]
                  [:mdsd-profiles nil]])
  (register-fns [[:clean mdsd-clean]
                 [:init mdsd-init]
                 [:pre-generate mdsd-pre-generate]
                 [:generate mdsd-generate]
                 [:generate mdsd-post-generate]]))
