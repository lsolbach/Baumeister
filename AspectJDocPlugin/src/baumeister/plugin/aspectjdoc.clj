(ns baumeister.plugin.aspectjdoc
  (:use [baumeister.utils files ant-utils checks log]
        [baumeister.config registry]))

; TODO fix classpath, add aspectjtools.jar to plugin dependencies
(ant-taskdef {:name "ajdoc"
              :classname "org.aspectj.tools.ant.taskdefs.Ajdoc"})
(define-ant-task ant-ajdoc ajdoc)

(defn aspectjdoc-sourcedoc
  []
  (when-let [source-dirs (seq (source-dirs :aspectj-source-dir))]
    (message :fine "generating aspectjdoc...")
    (ant-ajdoc {:destdir (param "${aspectjdoc-dir}")
                 :sourcepath (dir-path source-dirs)
                 :source (param :aspectj-source-version)
                 :classpath (jar-path (param :aspectj-lib-path))
                 :windowtitle (param :aspectjdoc-windowtitle)
                 :doctitle (param :aspectjdoc-doctitle)
                 :header (param :aspectjdoc-header)
                 :footer (param :aspectjdoc-footer)})))

(def config
  {:params [[:aspectjdoc-dir "${build-sourcedoc-dir}/aspectj"]
            [:aspectjdoc-windowtitle "${module} ${version}"]
            [:aspectjdoc-doctitle "${module} ${version}"]
            [:aspectjdoc-header "${module} ${version}"]
            [:aspectjdoc-footer ""]]
   :steps [[:sourcedoc aspectjdoc-sourcedoc]]
   :functions []})
