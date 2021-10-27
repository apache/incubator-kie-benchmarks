# Kie Server JMH Performance Tests
This module uses JMH as a basis for the remote workbench scenarios kept in org.jbpm.test.performance.scenario.
It is recommended to run this test suit using Jenkins Job.

## Execution:
* Start Kie Server
* Build benchmarks.jar using `mvn clean install`
* Run jar file using  
  `java -jar path_to_jar JMH_PARAMS -p remoteAPI=REST -jvmArgsAppend '-Dkieserver.host="" -Dkieserver.username="" -Dkieserver.password="" -Dkieserver.port=8080 -Dkieserver.name="" -Ddatasource.jndi= -DremoteAPI=REST ' -rff results.csv`

## JMH Options
JMH options are before the test method in each test. These are overridden with arguments from the command line when executing a jar file. To learn more about JMH arguments see: https://github.com/guozheng/jmh-tutorial/blob/master/README.md