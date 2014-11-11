;
; This settings.clj file contains the user specific settings for Baumeister.
; These settings override the defaults in $BAUMEISTER_HOME/config/module_defaults.clj
; and are merged with the settings in the module.clj files.
;
; The format is <key> <value> where keys start with a colon and values are strings or numbers.
; 
; You can use the variables that are defined in $BAUMEISTER_HOME/config/module_defaults.clj
; but especially these variables should be useful:
;
; ${user-home-dir} - the home directory  of the user ($HOME) 
; ${baumeister-home-dir} - the root directory of the running Baumeister instance ($BAUMEISTER_HOME)
;
; Copy this file to the directory $HOME/.Baumeister/ and customize it as needed
; 
[
 ; uncomment and set to the Java installation dir, if the JAVA_HOME environment variable is not set 
 ;:java-home ""
  
 ; uncomment and set to the AspectJ installation dir, if the ASPECTJ_HOME environment variable is not set 
 ;:aspectj-home ""
 
 ; uncomment and set to the Graphviz installation dir, if the 'dot' command is not on the PATH
 ;:graphviz_home ""
 
 ; uncomment and configure to use a proxy for HTTP/HTTPS requests
 ;http-proxy-bybasshosts "www.example.org"
 ;http-proxy-host "proxy"
 ;http-proxy-port 3128
 ;https-proxy-host "proxy"
 ;https-proxy-port 3128
 ]
