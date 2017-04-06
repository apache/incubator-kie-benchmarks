How to run the benchmarks
==========================

The benchmarks are written using [JMH](http://openjdk.java.net/projects/code-tools/jmh/).

To run the benchmarks you need to:  

1. Build the project with `mvn clean install`
2. Run the built project uberjar with `java -jar target/drools-benchmarks.jar

You can define several JMH parameters for executing the benchmarks:  
`-jvm` - Custom JVM to use when forking (path to JVM executable). JMH can create new JVM fork for a set of benchmark iterations.  
`-jvmArgs` - Custom JVM parameters. E.g. you can specify memory consumption arguments here.    
`-gc` - Option to force JMH to run garbage collection between benchmark iterations. Boolean parameter.  
`-foe` - Option to determine if JMH should fail immediately if any benchmark had experienced some unrecoverable error. Boolean parameter.  
`-wi` - Number of warmup iterations - Should be used only for "tweaking" the benchmarks run. Default value that is recommended to be used is set directly in the benchmarks' code using annotation `@Warmup`.  
`-i` - Number of measurement iterations - Should be used only for "tweaking" the benchmarks run. Default value that is recommended to be used is set directly in the benchmarks' code using annotation `@Measurement`.  
`-f` - Number that defines how many times should be a single benchmark forked. This defines how many JVM forks are used for measuring single benchmark. Default JMH value is 10.  
`-rf` - Results format type. E.g. csv.  
`-rff` - Filename to which results are written.  
- You can also define a wildcard pattern, that specifies, which benchmarks should be run.  
  
**Examples**  
  
Running all benchmarks from `org.drools.benchmarks.cep` package and storing results in file `results.csv`:  
  
`java -jar target/drools-benchmarks.jar -jvmArgs "-Xms4g -Xmx4g" -foe true -rf csv -rff results.csv org.drools.benchmarks.cep.*`  
  
Running benchmark `org.drools.benchmarks.operators.EvalBenchmark` and storing results in file `results.csv`:  
  
`java -jar target/drools-benchmarks.jar -jvmArgs "-Xms4g -Xmx4g" -foe true -rf csv -rff results.csv org.drools.benchmarks.operators.EvalBenchmark`  
  