(ns org.soulspace.build.baumeister.plugins.architecture
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search]
        [org.soulspace.clj.modelgenerator.generator]
        [org.soulspace.build.baumeister.utils ant-utils log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

; TODO better align with mdsd plugin?

(defn generators []
  (load-file (param "${architecture-config-dir}/${architecture-config-file}")))

; TODO find required profiles by a profile search path?
(defn generation-context []
  {:model-type "architecture-model"
   :model (param "${architecture-model-dir}/${architecture-model-name}")
   :destDir (param :architecture-generation-dir)
   :backupDir (param :architecture-backup-dir)
   :templateDirs (param :architecture-template-path)
   :profiles (map #(str (param :architecture-profile-dir) "/" %) (param :architecture-profiles))})

(defn models []
  (map file-name (list-files (as-file (param :architecture-generated-models-dir)))))

(defn modules []
  (map file-name (list-files (as-file (param :architecture-generated-modules-dir)))))

(defn architecture-clean []
  "architecture clean"
  (delete-file (as-file (param :architecture-backup-dir)))
  (delete-file (as-file (param :architecture-generation-dir))))

(defn architecture-pre-init []
  "architecture pre-init"
  (if-not (= (param :type) "architecture") ; validate module type
    (throw (RuntimeException. (str "Module is not of type architecture but of type " + (param :type))))))

(defn architecture-init []
  "architecture init"
  (create-dir (as-file (param :architecture-generation-dir)))
  (create-dir (as-file (param :architecture-backup-dir))))

(defn architecture-pre-generate []
  (log :info "preparing architecture generation...")
  (ant-delete {}
              (ant-fileset {:dir (param :architecture-backup-dir)}))
  (ant-copy {:todir (param :architecture-backup-dir) :overwrite "true"}
            (ant-fileset {:dir (param :architecture-generation-dir)}))
  (ant-delete {}
              (ant-fileset {:dir (param :architecture-generation-dir)}))
  (ant-unzip {:src (param "${architecture-model-dir}/${architecture-model-name}.zargo")
              :dest (param "${architecture-model-dir") :overwrite "true"}
             (ant-fileset {:dir "." :includes (param "${architecture-model-name}.xmi")})))

(defn architecture-generate []
  (generate-all (generation-context) (flatten (generators))))

(defn architecture-post-generate []
  (log :info "finishing architecture generation...")
  (log :debug "generated models" (models))
  (log :debug "generated modules" (modules))
  (doseq [model (models)] ; package models
    (log :debug "packaging model" model)
    (ant-zip {:destFile (str (param :architecture-generated-modules-dir) "/" model "/model/" model ".zargo")
              :basedir (str (param :architecture-generated-models-dir) "/" model)}))
  (doseq [module (modules)] ; package modules
    (log :info "packaging module" module)
    (ant-zip {:destFile (str (param :dist-dir) "/" module ".zip")
              :basedir (str (param :architecture-generated-modules-dir) "/" module)})))

; TODO when handling plugins as dependencies, move to plugin module.clj
(def config
  {:params [[:lib-generator-dir "${lib-dir}/generator"]
            [:lib-model-dir "${lib-dir}/model"]
            [:architecture-model-dir "${module-dir}/model"]
            [:architecture-model-name "${module}"]
            [:architecture-generation-dir "${module-dir}/generated"]
            [:architecture-backup-dir "${module-dir}/backup"]
            [:architecture-config-dir "${lib-generator-dir}/config"]
            [:architecture-config-file "architecture_generators.clj"]
            [:architecture-template-path ["${lib-generator-dir}/std-templates2" "${lib-generator-dir}/templates2"]]
            [:architecture-profile-dir "${lib-generator-dir}/profiles"]
            [:architecture-profiles ["argouml/default-uml14.xmi" "MDSDProfile.xmi"]]
            [:architecture-generated-models-dir "${architecture-generation-dir}/models"]
            [:architecture-generated-modules-dir "${architecture-generation-dir}/modules"]]
   :functions [[:clean architecture-clean]
               [:pre-init architecture-pre-init]
               [:init architecture-init]
               [:pre-generate-architecture architecture-pre-generate]
               [:generate-architecture architecture-generate]
               [:post-generate-architecture architecture-post-generate]]})

(defn plugin-init []
  (log :info "initializing plugin architecture")
  (register-vars (:params config))
  (register-fns (:functions config)))
