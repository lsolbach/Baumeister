{:targets 
 #{
   "root" ; root project dependency, not a target!
   "runtime" ; runtime dependency, used on compile and runtime classpaths
   "dev" ; dev dependency, used on compile and test classpaths
   "aspect" ; aspect dependency, used on aspect compile and runtime classpaths
   "aspectin" ; aspectin dependency, used on aspectin compile classpath
   "model" ; model dependency, used on generation profile path
   "generator" ; generator dependency, used on generation template and profile path
   "dependency" ; dependency only, no artifacts
   "meta" ; meta dependency, no artifacts
   "exclude" ; exclude dependency, exclude artifacts from dependency subtree
   }
 :actions
 {:copy #{"runtime" "dev" "aspect" "aspectin" "model"} ; copy the artifact to the specified lib target dir
  :unzip #{"generator"} ; unzip the artifact to ${lib-dir}
  :follow #{"dependency" "root" "meta"} ; follow transient dependencies only, do nothing for the artifact
  :exclude #{"exclude"} ; exclude the artifact and don't follow transient dependencies
  }}
