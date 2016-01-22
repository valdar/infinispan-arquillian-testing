# infinispan-arquillian-testing
An exmple project showing a testing approach with arquillian for infinspan in a j2ee context.

# Hot to build and run
For builing and running the project you need maven 3 installed and this in your m2/settings.xml:
```xml
<profiles>
    <id>jboss-ga-repository</id>
	<profile>
	  <id>jboss-ga-repository</id>
	  <repositories>
	    <repository>
	      <id>jboss-ga-repository</id>
	      <url>http://maven.repository.redhat.com/techpreview/all</url>
	      <releases>
		<enabled>true</enabled>
	      </releases>
	      <snapshots>
		<enabled>false</enabled>
	      </snapshots>
	    </repository>
	  </repositories>
	  <pluginRepositories>
	    <pluginRepository>
	      <id>jboss-ga-plugin-repository</id>
	      <url>http://maven.repository.redhat.com/techpreview/all</url>
	      <releases>
		<enabled>true</enabled>
	      </releases>
	      <snapshots>
		<enabled>false</enabled>
	      </snapshots>
	    </pluginRepository>
	  </pluginRepositories>
	</profile>
    </profiles>
    <activeProfiles>
	<activeProfile>jboss-ga-repository</activeProfile>
    </activeProfiles>
```
Then test and build it with:
```bash
$ mvn clean install package
```
# How is structured
The project is structured as a 3 submodule maven project, one to build the ejb part, one the web part and the third one to package all together in a ear.

The web layer expos 2 rest operations to write and read key-values pairs, using the service exposed by the ejb layer to actually store and read them from an infinispan remote instance.

# How can we test it
Even a quite trivial project like this has quite a bit of moving parts: the application server in which the app is deployed (web layer + service layer) and the distributed cache (infinispan) in which data are persisted.

To implement a possible unit test approach arquillian has been used ([http://arquillian.org/](http://arquillian.org/)). 
Arquillian can run in 3 different modes:
-  **in memory**: as it say the test environment is run in the same jvm (embedded) as the tests. Not so many application servers supported in this mode.
- **standalone**: the application server is already started and arquillian interact with it to deploy, run and undelpoy stuff. This is not suitable for a unit test.
- **managed**: in this mode arquillian start, deploy, run, undelpoy and stop the server. It only need to know where is installed (with some care this mode can be used to implement a unit test i.e.: make sure to place the install directory of the pplication server inside target/). 

We are going to concentrate on the ejb module tests since is the one actually using infinispan. The important bits are a little bit scattered around:
* maven plugin to unzip the application server in target: can be found in *ejb/pom.xml*:
```xml
         <plugin>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>unpack</id>
                        <phase>process-test-classes</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>org.wildfly</groupId>
                                    <artifactId>wildfly-dist</artifactId>
                                    <version>8.1.0.Final</version>
                                    <type>zip</type>
                                    <overWrite>false</overWrite>
                                    <outputDirectory>target</outputDirectory>
                                </artifactItem>
                                <artifactItem>
                                    <groupId>org.infinispan.server</groupId>
                                    <artifactId>infinispan-server-build</artifactId>
                                    <version>8.0.1.Final</version>
                                    <type>zip</type>
                                    <overWrite>false</overWrite>
                                    <outputDirectory>target</outputDirectory>
                                </artifactItem>
                            </artifactItems>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
this will extract both wildfly and infinispan in the *target/* directory before the tests are ran.
* the arquillian config file to tell arquillian where to find the severs and how to interact with them: can be found in *ejb/src/test/resources/arquillian.xml*:
```xml
<arquillian xmlns="http://jboss.org/schema/arquillian"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="
        http://jboss.org/schema/arquillian
        http://jboss.org/schema/arquillian/arquillian_1_0.xsd">
    <defaultProtocol type="Servlet 3.0" />
    <group qualifier="wildfly-managed" default="true">
        <container qualifier="grid">
            <configuration>
                <property name="jbossHome">target/infinispan-server-8.0.1.Final</property>
                <property name="javaVmArguments">-Djboss.socket.binding.port-offset=100 -Djboss.node.name=nodeA</property>
                <property name="managementPort">10090</property>
            </configuration>
        </container>
        <container qualifier="as" default="true">
            <configuration>
                <property name="jbossHome">target/wildfly-8.1.0.Final</property>
                <property name="javaVmArguments">-Djboss.socket.binding.port-offset=200 -DJDG_SERVERLIST=127.0.0.1:11322 -DSEARCHDEAL_CACHE_NAME=default -DJDG_PROTOCAL_VERSION=2.2</property>
                <property name="managementPort">10190</property>
            </configuration>
        </container>
    </group>
</arquillian>
```  
here one can define almost everything for the starting up of the servers (note that there are 2 of them with different names "grid" and "as", the default one is the latter which means it will be used to actually deploy code and tests).
* the actual tests: in *ejb/src/java/test/com/github/valdar/iat*. In *TestUtils* class is defined the structure of the ear package that is going to be used in the *GridServiceTest* to actually perform the test.

# Some things to be note
In this type of unit tests you are actually spinning up a wildfly instance and an infinispan one, build through an api an ear (containing the classes you want to test, the tests and all the other resources you might need), deploy it on wildfly and run the test. 
The test itself calls the service that in turn has been instantiated as a singleton at application's startup and is connected to the infinispan instance.