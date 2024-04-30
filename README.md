# Supplementary materials for the work Adapting Multi-objectivized Software Configuration Tuning
This repository contains the data and source code for the accepted paper:

> Tao Chen and Miqing Li. 2024. Adapting Multi-objectivized Software Configuration Tuning. FSE’24: Proceedings of the ACM on Software Engineering (PACMSE) 1, FSE. https://doi.org/10.1145/3643751




## Data Result

The dataset of this work can be accessed via the Zenodo link [here](https://zenodo.org/record/8252997). The zip file contains all the raw data as reported in the paper; most of the structures are self-explained but we wish to highlight the following:

* The data under the folder `1.0-0.0` and `0.0-1.0` are for the single-objective optimizers, including IRACE, GA, RS, ParamILS, FLASH, BOCA, and SMAC. The former uses the first objective as the target performance objective while the latter uses the second objective as the target.

* The data in the folder `none` under the folders named by the subject systems are for PMO.

* The data in the folder `linear` which is under the folders named by the subject systems are for the AdMMO and MMO. In particular:

  * The data under the folder `0.0` are for AdMMO (30% proportion).
  * The data under the folder `1.0` are for MMO.
  * The result under the folder `0.0-x`, where x is a certain number for AdMMO under x% of the proportion.
  * The data under the folder `0.0-wodup` are for the variant of AdMMO that indistinguishes duplicate configurations.
  * The data under the folder `0.0-removedup` are for the variant of AdMMO that simply removes all but one duplicate. 
  * The data under the folder `0.0-wotrigger` are for the variant of AdMMO that does not use a progressive trigger

* For those data of AdMMO, MMO, and PMO, the folder `0` and `1` denote using uses the first and second objectives as the target performance objective, respectively.

* In the lowest-level folder where the data is stored (i.e., the `sas` folder), `SolutionSet.rtf` contains the results over all repeated runs; `SolutionSetWithMeasurement.rtf` records the results over different numbers of measurements.

## Souce Code

The [`code`](https://github.com/3c23/admmo/tree/main/code) folder contains all the information about the source code, as well as a library jar file in the [`library`](https://github.com/3c23/admmo/tree/main/library) folder. The core of AdMMO is implemented in the `AdMMONSGAII.java` class, but due to it using some libraries that may violate the double-blind requirement, we temporarily hide some of the code import those packages.


