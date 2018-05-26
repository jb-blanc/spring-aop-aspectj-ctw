# Spring and AspectJ Compile time weaving

A common problem with Spring AOP is getting Pointcut reeaching inner method call of a class.

In this example, you can see how to handle coompile time weaving with AspectJ in a simple spring boot example.

## Example scenario
In this example here's what happens : 

 - We call our application on http://localhost:8080/test
 - MyRest.startComputation will be called but no console output because no annotation LogMe positioned
 - **MyRest.firstMethod** will be call
 - **MyRest.firstFirstMethod** will be call within *MyRest.firstMethod*
 - **MyRest.firstSecondMethod** will be call within *MyRest.firstMethod*

## First : pom.xml

Add aspectjrt dependency in pom.xml : 

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
</dependency>
```

Add spectj compile plugin : 

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>aspectj-maven-plugin</artifactId>
            <version>1.10</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <proc>none</proc>
                <complianceLevel>${java.version}</complianceLevel>
                <showWeaveInfo>true</showWeaveInfo>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>org.aspectj</groupId>
                    <artifactId>aspectjtools</artifactId>
                    <version>${aspectj.version}</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

### Note ###
You can deactivate ```<shoWeaveInfo>``` if you want, but it's really usefull during development to see join points defined by your advices. The console output on compile goal will look like this : 

```
[INFO] --- aspectj-maven-plugin:1.10:compile (default) @ springaspectj ---
[INFO] Showing AJC message detail for messages of types: [error, warning, fail]
[INFO] Join point 'method-execution(void fr.jibibi.springaspectj.application.MyRest.firstMethod())' in Type 'fr.jibibi.springaspectj.application.MyRest' (MyRest.java:16) advised by around advice from 'fr.jibibi.springaspectj.apects.JibibiAspects' (JibibiAspects.java:13)
[INFO] Join point 'method-execution(void fr.jibibi.springaspectj.application.MyRest.firstFirstMethod())' in Type 'fr.jibibi.springaspectj.application.MyRest' (MyRest.java:23) advised by around advice from 'fr.jibibi.springaspectj.apects.JibibiAspects' (JibibiAspects.java:13)
[INFO] Join point 'method-execution(void fr.jibibi.springaspectj.application.MyRest.firstSecondMethod())' in Type 'fr.jibibi.springaspectj.application.MyRest' (MyRest.java:28) advised by around advice from 'fr.jibibi.springaspectj.apects.JibibiAspects' (JibibiAspects.java:13)
```

And we're done with the pom.xml

## Second : Disable spring aop auto
Go to your application.properties and disable spring aop autoconfiguration.
```spring.aop.auto=false```

## Third : Create aop.xml
For aspectj to execute correctly the compilation weaving, you need to create a new file named **aop.xml** into ```src/main/resources/org.aspectj/```

Here is the current definition for this example : 

```xml
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "http://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>
	<weaver>
		<include within="fr.jibibi.springaspectj..*" />
		<include within="org.springframework.boot..*" />
	</weaver>

	<aspects>
		<aspect name="fr.jibibi.springaspectj.aspects.JibibiAspects" />
	</aspects>

</aspectj>
```

The **weaver** section list the packages that should be used in the weaving process.
Don't know why yet, but you need to include springboot package in here.

The **aspectj** section is for declaring all your aspect's classes.

## Last but not least

All you have to do now, is to compile your application and run it ;)
It's really important to run the compilation before running because of the compilation time waeving. All your Pointcuts will be processed and analysed on the compilation.


### Expected console output

```
Before Method : firstMethod
Method 1
Before Method : firstFirstMethod
Method 1.1
After Method : firstFirstMethod
Before Method : firstSecondMethod
Method 1.2
After Method : firstSecondMethod
After Method : firstMethod
```

# Encountered problems

## Advice called twice
In the process of making this example, I've made a mistake that used to prompt 2 times each advice call.

**Reason :** My pointcut wasn't precise enough. I only had the @annotation in my pointcut and so, the compile time weaving detected one trigger for the method call and another one for the method execution.

**Solution :** Add execution(...) in my pointcut

## My new advice/pointcut is not triggered
Multiple times, i've tried to tweak a bit my pointcut or create other advices, but they were not executing even if they were corrects.

**Reason :** COMPILATION !!! It may seems dumb, but I didn't recompile using maven and so, the compiler plugin.

**Solution :** Run your maven compile goal and it'll be ok.