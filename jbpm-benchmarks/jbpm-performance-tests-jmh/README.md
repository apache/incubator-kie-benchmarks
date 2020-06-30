# JBPM Engine Performance Tests using JMH framework

This module uses the JMH framework as a basis for the jbpm engine scenarios kept in org.jbpm.test.performance.scenario.

## Execution:
* Build benchmarks.jar using `mvn clean install`
* Run jar file using  
`java -jar path_to_jar -jvmArgsAppend "-Dcom.arjuna.ats.arjuna.common.propertiesFile=jbossts-properties.xml -Djbpm.persistence=true -Djbpm.locking=optimistic -Djbpm.ht.eager=true" -p runtimeManagerStrategy=Singleton -rf csv -rff results.csv`
* To run only single test use
`java -jar path_to_jar test_name_to_match -jvmArgsAppend "-Dcom.arjuna.ats.arjuna.common.propertiesFile=jbossts-properties.xml -Djbpm.persistence=true -Djbpm.locking=optimistic -Djbpm.ht.eager=true" -p runtimeManagerStrategy=Singleton -rf csv -rff results.csv`

## JBPM Engine Options
* Runtime Manager Strategy = Singleton, PerProcessInstance, PerRequest
* Persistence = true/false
* JBPM Locking = optimistic/pessimistic
* Human Task Eager = true/false

## JMH Options
JMH options are before the test method in each test. These are overridden with arguments from the command line when executing a jar file. To learn more about JMH arguments see: https://github.com/guozheng/jmh-tutorial/blob/master/README.md
