(ns baumeister.plugin.javadoc
  (:use [baumeister.utils files ant-utils checks log]
        [baumeister.config registry]))

(defn javadoc-sourcedoc
  []
  (when-let [source-dirs (seq (source-dirs :java-source-dir))]
    (message :fine "generating javadoc...")
    (ant-javadoc {:destdir (param "${javadoc-dir}")
                :sourcepath (dir-path source-dirs)
                :source (param :java-source-version)
                :classpath (jar-path (param :java-lib-path))
                :windowtitle (param :javadoc-windowtitle)
                :doctitle (param :javadoc-doctitle)
                :header (param :javadoc-header)
                :footer (param :javadoc-footer)})))

(def config
  {:params [[:javadoc-dir "${build-sourcedoc-dir}/java"]
            [:javadoc-windowtitle "${module} ${version}"]
            [:javadoc-doctitle "${module} ${version}"]
            [:javadoc-header "${module} ${version}"]
            [:javadoc-footer ""]]
   :steps [[:sourcedoc javadoc-sourcedoc]]
   :functions []})
