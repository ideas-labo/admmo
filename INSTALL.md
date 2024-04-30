## Running Code

Running the code requires a few steps depending on the RQs. For all those steps, both the jars files in the `library` folders need to be imported and compiled together. There are a few variables that need to change in the `AutoRun.java` class (which is the main class to run the experiments):

* The variable `weights` indicates which is the target performance objective for the single objective optimizer, 1.0-0.0 means the first objective while 0.0-1.0 means the second objective.
* The variable `single_algs` controls which single objective optimizer to run: irace, ga, paramils, or rs.
* The variable `benchmark` describes which system to run, currently, it can take the following values:
  * mariadb
  * storm
  * vp9
  * keras
  * mongodb
  * x264
  * llvm
* The variable `index` indicates which is the target objective, 0 means the first objective while 1 means the second objective.
* The variable `w_a` controls whether it is AdMMO or MMO: 0.0 is for AdMMO while 1.0 is for MMO.
* The variable `flash` controls whether we run different variants of AdMMO.

The `prefix_path` variable in the `Parser.java` class would also need to be changed to correctly reflect which system to run, using the path to the data source in the [`measured-data`](https://github.com/3c23/admmo/tree/main/measured-data) folder. 

### RQ1 and RQ2

* Running function `so` would produce results for single-objective optimizers.
* Running function `pmo` would produce results for PMO.
* Running function `mo` would produce results for AdMMO or MMO (controlled by the `w_a` variable).
* Running function `flash_so` would produce results for FLASH
* Running function `boca_so` would produce results for BOCA.
* Running function `smac_so` would produce results for SMAC.

### RQ3

* Running function `mo` would produce results for the variants of AdMMO (controlled by the `flash` variable) and by setting `SASAlgorithmAdaptor.isAdaptConstantly = false` or `SASAlgorithmAdaptor.isToFilterRedundantSolution = false`.

### RQ4

* Running function `mo` would produce results for the variants of AdMMO (controlled by the `flash` variable) and by setting the variable `p` or in the `AdMMONSGAII.java` class.
