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

## Goal
 - combination matrix of different datasets, different heuristics and different moves
   - different data → we need to test simple problems, chaining, chaining with shadow, two variable problems ...
   - datasets have exponential growth → how OptaPlanner scales on different datasets?
   - different heuristics → did some commit slow down heuristics?
   - different move selectors → did some commit slow down some particular move selector?
 - find any possible planner's performance regressions


## Source code
 - how to run:

   ```
$ mvn clean install
$ java -Xms6144m -Xmx6144m -XX:PermSize=512m -XX:MaxPermSize=768m \
-jar brms-optaplanner-perf-benchmark/target/optaplanner-benchmarks.jar \
-foe true -wi 5 -i 10 -f 10 -rf csv -rff results.csv
   ```
     - to see what args mean, check https://mojo.redhat.com/docs/DOC-150055

## Jenkins job
 - https://jenkins.mw.lab.eng.bos.redhat.com/hudson/view/BxMS/view/BxMS-6.2/view/OptaPlanner/view/all/job/brms-6.2-optaplanner-perf

 - performance tests takes ~ 1.5 day to execute!
   - if you can figure out this issue, you are welcome to make this process faster :)
         https://stackoverflow.com/questions/33734266/is-it-possible-to-run-time-based-warmup-phase-using-jmh
