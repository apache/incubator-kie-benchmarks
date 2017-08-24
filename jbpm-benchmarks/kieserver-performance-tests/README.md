# Kie Server Performance Tests
This module uses kie-performance-kit as a basis for the remote workbench scenarios kept in org.jbpm.test.performance.scenario.

The tests should be executed via `run.sh` bash script since this script is by default used for running selected test suite when no scenario is selected.

1. Setup JBoss EAP 6
2. Setup KIE Performance Kit and Kie Server configuration in `pom.xml`
3. [optional] Configure JVM in `pom.xml` by adding arguments into *exec-maven-plugin*
4. Execute `run.sh` or manually *mvn clean install exec:exec -Dscenario=[scenario]*

## JBoss EAP 6 Setup

1. Set system property JBOSS_HOME `export JBOSS_HOME=<EAP6 location>`
2. Execute `mvn clean install` in kieserver-performance-tests to add application users
3. Start the application server with deployed kie-server
4. Execute `deploy.sh` in kieserver-assets module to build and deploy the kjar containing business processes and business rules for performance testing

## KIE-Performance-Kit Setup


All configuration goes into `pom.xml`

* Select suite and scenario
 * Available suites = `LoadSuite, ConcurrentLoadSuite`
 * To run the whole suite, comment scenario property to be null and make sure that `startScriptLocation` is set correctly.
* Select run type of the test suite
 * Available run types - `Duration, Iteration`
 * If Duration run type is chose, every scenario will run given given time according to time set into `duration` property (measured in seconds).
 * If Iteration run type is chose, every scenario will run given number of iterations according to number set into `iterations` property.
* Turn ON/OFF warm up before scenario
 * To enable warm up, set true to `warmUp` property.
 * Every scenario will run X times according to `warmUpCount` property
* Running scenarios concurrently in threads
 * When ConcurrentLoadSuite is used we are able to set number threads in which the test suites should run.
 * The total number of tests = `threads` * iterations
* Reporting
 * Types of reporters = Console, CSVSingle, CSV
 * `Console`
 * `CSV` - reports periodiacally after X seconds into CSV files for every metrics
 * `CSVSingle` - reports scenario CSV files containing the metrics results
 * `PerfRepo` - creates test definitions and test executions, reports results to remote PerfRepo (requires JDK 1.8, see https://github.com/PerfRepo/PerfRepo)
* Additional metrics
 * `MemoryUsage` - HEAP and pools usage (Eden, Old Gen, etc.)
 * `ThreadStates` - number of threads and their states (waited, runnable, new, blocked, etc.), deadlocks, ...
 * `FileDescriptors` - number of opened descriptors, percentage of usage
 * `CPUUsage` - histogram of cpu usage of the process/scenario (count, min, max, mean, median, ...) useful only when scenario iterations/duration takes longer than 300ms
 * Set any of the metrics above as a list into *measure* property

## Kie Server KIE-Performance-Kit Setup

* remoteAPI = `REST` or `JMS` (it's not possible to test both at once)
* username - the main application user for most of the performance tests

