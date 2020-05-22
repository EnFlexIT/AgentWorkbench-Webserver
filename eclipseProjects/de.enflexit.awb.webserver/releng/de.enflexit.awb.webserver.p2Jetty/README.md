# Execution of the projects pom.xml 

### Problem Description
Integrating Jetty in an Eclipse RCP / OSGI environment (or application) seems to be not trivial. Simply integrating the 
[Jetty p2 repository](http://download.eclipse.org/jetty/updates/jetty-bundles-9.x) ([see eclipse site](https://www.eclipse.org/jetty/download.html)) 
in your target platform definition results to errors that say that required packages, or bundles respectively, can not be found:

![TargetPlatformError](./images/TargetPlatform.png "Target Platform Error")  

The reason seems to be that only the Jetty bundles are provided but not the required pure java *.jar files like **javax.annotation-api_1.2.0.jar** 
and others (?) or that they are available but not as OSGI bundles. However, to use Jetty in your RCP / OSGI application, it would be preferable 
to have all Jetty bundles as a single feature in your target platform.


### Things tried


 

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