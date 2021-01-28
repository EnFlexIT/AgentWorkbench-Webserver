# Set Target Platform ready to work

To load the required Jetty OSGI-bundles into your target platform, run **maven install** on the **pom.xml** beside this file.  
This will download the configured Jetty files (see pom.xml) from the central Maven repository to the directory **./jettyJars**.  
Afterwards, set the target platform definition **de.enflexit.awb.webserver.target.target** as **Active Target Platform** or  
reload the target platform definition.    
