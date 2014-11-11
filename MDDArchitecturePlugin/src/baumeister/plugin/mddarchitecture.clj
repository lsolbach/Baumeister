;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.mddarchitecture
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file file-search]
        [org.soulspace.clj.modelgenerator.generator]
        [baumeister.utils ant-utils log]
        [baumeister.config registry]))

; TODO better align with mdsd plugin?

(defn generators []
  (load-file (param "${mddarchitecture-config-dir}/${mddarchitecture-config-file}")))

; TODO find required profiles by a profile search path?
(defn generation-context []
  {:model-type "mddarchitecture-model"
   :model (param "${mddarchitecture-model-dir}/${mddarchitecture-model-name}")
   :destDir (param :mddarchitecture-generation-dir)
   :backupDir (param :mddarchitecture-backup-dir)
   :templateDirs (param :mddarchitecture-template-path)
   :profiles (map #(str (param :mddarchitecture-profile-dir) "/" %) (param :mddarchitecture-profiles))})

(defn models []
  (map file-name (files (as-file (param :mddarchitecture-generated-models-dir)))))

(defn modules []
  (map file-name (files (as-file (param :mddarchitecture-generated-modules-dir)))))

(defn mddarchitecture-clean []
  "mddarchitecture clean"
  (delete-file (as-file (param :mddarchitecture-backup-dir)))
  (delete-file (as-file (param :mddarchitecture-generation-dir))))

(defn mddarchitecture-pre-init []
  "mddarchitecture pre-init"
  (if-not (= (param :type) :architecture) ; validate module type
    (throw (RuntimeException. (str "Module is not of type architecture but of type " + (param :type))))))

(defn mddarchitecture-init []
  "mddarchitecture init"
  (create-dir (as-file (param :mddarchitecture-generation-dir)))
  (create-dir (as-file (param :mddarchitecture-backup-dir))))

(defn mddarchitecture-pre-generate []
  (log :info "preparing architecture generation...")
  (ant-delete {}
              (ant-fileset {:dir (param :mddarchitecture-backup-dir)}))
  (ant-copy {:todir (param :mddarchitecture-backup-dir) :overwrite "true"}
            (ant-fileset {:dir (param :mddarchitecture-generation-dir)}))
  (ant-delete {}
              (ant-fileset {:dir (param :mddarchitecture-generation-dir)}))
  (ant-unzip {:src (param "${mddarchitecture-model-dir}/${mddarchitecture-model-name}.zargo")
              :dest (param "${mddarchitecture-model-dir") :overwrite "true"}
             (ant-fileset {:dir "." :includes (param "${mddarchitecture-model-name}.xmi")})))

(defn mddarchitecture-generate []
  (generate-all (generation-context) (flatten (generators))))

(defn mddarchitecture-post-generate []
  (log :info "finishing architecture generation...")
  (log :debug "generated models" (models))
  (log :debug "generated modules" (modules))
  (doseq [model (models)] ; package models
    (log :debug "packaging model" model)
    (ant-zip {:destFile (str (param :mddarchitecture-generated-modules-dir) "/" model "/model/" model ".zargo")
              :basedir (str (param :mddarchitecture-generated-models-dir) "/" model)}))
  (doseq [module (modules)] ; package modules
    (log :info "packaging module" module)
    (ant-zip {:destFile (str (param :dist-dir) "/" module ".zip")
              :basedir (str (param :mddarchitecture-generated-modules-dir) "/" module)})))

; TODO when handling plugins as dependencies, move to plugin module.clj?
(def config
  {:params [[:lib-generator-dir "${lib-dir}/generator"]
            [:lib-model-dir "${lib-dir}/model"]
            [:mddarchitecture-model-dir "${module-dir}/model"]
            [:mddarchitecture-model-name "${module}"]
            [:mddarchitecture-generation-dir "${module-dir}/generated"]
            [:mddarchitecture-backup-dir "${module-dir}/backup"]
            [:mddarchitecture-config-dir "${lib-generator-dir}/config"]
            [:mddarchitecture-config-file "mddarchitecture_generators.clj"]
            [:mddarchitecture-template-path ["${lib-generator-dir}/templates" "templates"]]
            [:mddarchitecture-profile-dir "${lib-generator-dir}/profiles"]
            [:mddarchitecture-profiles ["argouml/default-uml14.xmi" "MDSDProfile.xmi"]]
            [:mddarchitecture-generated-models-dir "${mddarchitecture-generation-dir}/models"]
            [:mddarchitecture-generated-modules-dir "${mddarchitecture-generation-dir}/modules"]]
   :steps [[:clean mddarchitecture-clean]
           [:pre-init mddarchitecture-pre-init]
           [:init mddarchitecture-init]
           [:pre-generate-architecture mddarchitecture-pre-generate]
           [:generate-architecture mddarchitecture-generate]
           [:post-generate-architecture mddarchitecture-post-generate]]
   :functions []})
