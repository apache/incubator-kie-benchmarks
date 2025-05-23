How to run the benchmarks
==========================

The benchmarks are written using [JMH](http://openjdk.java.net/projects/code-tools/jmh/).

To run the benchmarks you need to:  

1. Build the project with `mvn clean install`
2. Run the built project uberjar with `java -jar target/drools-benchmarks.jar`

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


Tips
====

It is possible to debug the single benchmarks inside IntelliJ with the [JMH Java Microbenchmark Harness](https://plugins.jetbrains.com/plugin/7529-jmh-java-microbenchmark-harness) plugin. 
There are a couple of detail to fullfill:
1. Enable annotation processing (under Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors)
2. set `@Fork(0)` annotation, at least during debug

Flamegraph Generation
=====================
[Flame-graphs](https://www.brendangregg.com/flamegraphs.html) is a visualization tool used to visualize stack traces of profiled software so that the most frequent code-paths can be identified quickly and accurately.

[async-profiler](https://github.com/async-profiler/async-profiler) is a tool that, on the other side, create the flame-graphs files out of a running java process.


There are two possible approaches

#### External connect of async profiler

The overall idea is to:
1. start benchmarks
2. attach async-profiler to running benchmarks to generate flamegraph file
3. analyze flamegraphs file (that it is an html) inside a browser

Steps (Unix machine):
1. install async-profiler (OS-dependent)
2. enable kernel sampling (linux -  superuser):
´´´bash
   echo 1 > /proc/sys/kernel/perf_event_paranoid
   echo 0 > /proc/sys/kernel/kptr_restrict
´´´
3. start benchmark
4. retrieve _**ForkedMain**_ PID with jps
```bash
jps | grep ForkedMain
```
5. connect profiler to running instance
```bash
asprof -d 30 -f flamegraph.html <PID>
```
6. profiler arguments
   1. -d : timer duration in seconds
   2. -j: stack sampling deep
   3. -i: sampling interval; default to 10ms, but better to set it to 1ms


#### Run JMH with async profiler

A different approach could be to use the async profiler directly inside the benchmark execution.
Command is simply something like:

```bash
java -jar target/drools-benchmarks.jar -jvmArgs "-Xms24g -Xmx24g" -foe true -rf csv -rff results.csv -prof async:output=flamegraph org.drools.benchmarks.dmn.runtime.DMNEvaluateDecisionTableBenchmark
```

Flamegraphs will be generated inside a folder whose name depends on the benchmark executed
  
