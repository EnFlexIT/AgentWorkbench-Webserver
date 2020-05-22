# Jetty OSGI for Target Platforms / Execution of the projects pom.xml 

### Problem Description
Integrating Jetty in an Eclipse RCP / OSGI environment (or application) seems to be not trivial. Simply integrating the 
[Jetty p2 repository](http://download.eclipse.org/jetty/updates/jetty-bundles-9.x) ([see eclipse site](https://www.eclipse.org/jetty/download.html)) 
in your target platform definition results to errors that say that required packages, or bundles respectively, can not be found:

![TargetPlatformError](./images/TargetPlatform.png "Target Platform Error")  

The reason seems to be that only the Jetty bundles are provided but not the required pure java *.jar files like **javax.annotation-api_1.2.0.jar** 
and others (?) or that they are available but not as OSGI bundles. However, to use Jetty in your RCP / OSGI application, it would be preferable 
to have all Jetty parts as OSGI bundles concluded in a single feature for the usage within a target platform definition in Eclipse.


### Things we tried
Searching Google, Stack Overflow and the Eclipse Community Forum. Asking in the issue section of the [jetty.project](https://github.com/eclipse/jetty.project)
at GitHub and finally, giving up the search for several month. 

In the meantime we used the Jetty bundles that are provides by the current release in the Eclipse repository (e.g. https://download.eclipse.org/releases/2020-03/), but
here not all Jetty bundles are available, such as the XML, WebApp bundles and other. Those bundles we downloaded from the maven repository or used the bundles
that are provided with the Jetty distribution (see <https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/>).      
Unfortunately, we end up in an unsightly mixture of repository files and local files that were manually copied to a project directory.
This directory again were used within the target platform definition, which always resulted to errors when starting Eclipse and resolving the target platform bundles - ugly!   

### The current apporach 


### Execution of the projects pom 
 
 

The projects local pom-file basically serves as an **example** file for later use in different projects and 
should demonstrate how to use the *'p2-maven-plugin'*. Thus, it is not required to run the maven build, since
the required libraries will manually be placed in the projects '*/lib' directory.

  The specified *.pom file is configured to create a p2 update-site for all components required for JAXB.
It uses the maven *'p2-maven-plugin'* that enables to download non-OSGI jar files from the central maven 
repository, convert them into OSGI bundles and place them in a local p2 update-site.   

To do so, just execute the *pom.xml* with the argument

```
mvn clean p2:site i
```
or, if you are using Eclipse, right click on the *pom.xm* and select *'Run As'* => *'Maven Build ...'* and 
type **clean p2:site install** into the *Goals* section. 

For more information about the *'p2-maven-plugin'*, have a look at: <https://github.com/reficio/p2-maven-plugin>! 