{
 :clean-workflow [:clean]
 :init-workflow [:clean :init]
 :compile-workflow [:init :dependencies :generate :compile]
 :package-workflow [:build-workflow :package]
 :release-workflow [:build-workflow :release :distribute-release]
 :unittest-workflow [:unittest]
 :integrationtest-workflow [:integrationtest]
 :acceptancetest-workflow [:acceptancetest]
 :build-workflow [:clean :compile-workflow :unittest :package
                  :coverage :analyse :distribute]
 :architecture-workflow [:clean :init :dependencies :generate-architecture]
 :coverage-workflow [:coverage] 
 :analyse-workflow [:analyse]
; :create-module-workflow [:create-module]
 }
