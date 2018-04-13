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
 - repository: https://github.com/jboss-integration/optaplanner-perf-tests
 - how to run:

   ```
   $ mvn clean install
   $ java -Xms6144m -Xmx6144m -XX:PermSize=512m -XX:MaxPermSize=768m \
   -jar brms-optaplanner-perf-benchmark/target/optaplanner-benchmarks.jar \
   -foe true -wi 5 -i 10 -f 10 -rf csv -rff results.csv
   ```

     - to see what args mean, check https://mojo.redhat.com/docs/DOC-150055

## Jenkins job
 - https://rhba-qe-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/job/custom/job/jlocker/job/brms-optaplanner-perf-1.0/

 - performance tests takes ~ 1.5 day to execute!
   - if you can figure out this issue, you are welcome to make this process faster :)
         https://stackoverflow.com/questions/33734266/is-it-possible-to-run-time-based-warmup-phase-using-jmh
         
## Performance report
 - All results are uploaded to PerfRepo: http://perfrepo.mw.lab.eng.bos.redhat.com and can be referenced in any further 
 point in time.
 - Performance report templates as well as finished performance reports can be found 
 [here](https://drive.google.com/drive/folders/1EpbsD4fCmI0OBTZdFXXXyGODeQg3IZQJ). 
 - A new report can be created by following these steps:
   1. copy both spreadsheet and document template into a new subfolder
   2. upload CSV files with results for builds being compared from PerfRepo into the same subfolder
   3. reference both files in the spreadsheet
   4. open the document and refresh all the tables and charts
   5. provide analysis and comments on results
   6. convert the report into PDF and share it on Mojo to make it searcheable
