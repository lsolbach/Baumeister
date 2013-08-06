[
 :sites
 [
  :project-site ; multi modules
  [
   :overview
   [:module :project :version :license
    :modules [:module :version :description :module-site]
   ]
  :module-site
  [
   :content
   [
    :overview
    [:module :project :version :description :license :provider :homepage ; provider/organisation hompage/url
     :developers []
     :contributors []
     :scm []
     :issue-tracker []
     :distribution []
     ]
    :doumentation 
    [
     :userdoc
     :projectdoc
     :designdoc
     :apidoc
     ]
    :reports
    [
     :dependencies []
     :test-results
     [
      :unittest []
      :unittest-coverage []
      :integrationtest []
      :integration-coverage []
      :acceptancetest []
      :acceptance-coverage []
      ]
     :analysis
     [
      :jdepend []
      :pmd []
      :cpd []
      :findbugs []
      :checkstyle []
      ]
     ]
    ]
   :layout []
   ]
  ]
 ]