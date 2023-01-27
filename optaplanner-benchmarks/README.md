# OptaPlanner Performance Test Suite

Testing planner's performance regression in various tests.

| Dataset characteristics: ||
| --- | --- |
| cloud balancing               | simple example, 1 entity, 1 variable |
| traveling salesman problem    | simple example, 1 chaining entity, 1 chained variable |
| vehicle routing               | harder example, 1 chaining entity, 1 chained variable, 1 shadow variable |
| time windowed vehicle routing | harder example, 1 chaining entity, 1 chained variable, 2 shadow variable
| project job scheduling        | harder example, 1 entity, 2 variables, 1 shadow variable |

## What is tested
 - construction heuristics
   - First Fit & First Fit Decreasing
     - basically First Fit would be enough...
 - local search
   - heuristics
     - Hill Climbing, Late Acceptance, Simulated Annealing, Step Hill Climbing, Tabu Search
     - heuristics don't need much testing, since they are very easy concepts which just decide what move is picked
     - using **all possible move selectors combined** - so non-efficiency of one move selector is vanished, at least a little bit
   - move selectors
     - harder than heuristics
     - can vary marginally from solution to solution
     - Change, Pillar Change, Swap, Pillar Swap, Sub Chain Change, Sub Chain Swap, Tail Chain Swap moves
     - tested separately
       - some of them scale much worse than others on same dataset
         - different tuning for all tests is needed
    - constraint streams 

## Goal
 - combination matrix of different datasets, different heuristics and different moves
   - different data → we need to test simple problems, chaining, chaining with shadow, two variable problems ...
   - datasets have exponential growth → how OptaPlanner scales on different datasets?
   - different heuristics → did some commit slow down heuristics?
   - different move selectors → did some commit slow down some particular move selector?
 - find any possible planner's performance regressions


## Source code
 - repository: https://github.com/kie-group/kie-benchmarks/optaplanner-benchmarks
 - how to run:

   ```
   $ mvn clean install
   $ java -Xms6144m -Xmx6144m -XX:PermSize=512m -XX:MaxPermSize=768m \
   -jar optaplanner-perf-benchmark/target/optaplanner-benchmarks.jar \
   -foe true -wi 5 -i 10 -f 10 -rf csv -rff results.csv
   ```

     - to see what args mean, check https://mojo.redhat.com/docs/DOC-150055

## Additional parameters
- to run benchmarks on the certain version of OptaPlanner you need to provide version property e.g 

```
-Dversion.org.optaplanner=8.1.0.Final
```

## Idea users
 - to prevent downloading the kie-parent dependencies of the root module, you need to open just optaplanner-benchmarks module

## Performance profiling with JMH

You can run the profiling on jmh by adding -prof flag with the path to the profiler


For Example to create a flame graph with the async profiler, download async profiler lib and unzip it:
```
wget https://github.com/jvm-profiling-tools/async-profiler/releases/download/v2.9/async-profiler-2.9-linux-x64.tar.gz
tar -xvf async-profiler-2.9-linux-x64.tar.gz
```
Run tests with the profiler:
`java -jar optaplanner-benchmarks.jar -prof "async:output=flamegraph;event=cpu;libPath=./async-profiler-2.9-linux-x64/build/libasyncProfiler.so"`

