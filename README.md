# Java Sequential Solver - Mean State Data Collection

The Java Sequential Solver contains a sequential implementation of the PII algorithm. This is used as an input data generator that is used in conjunction with [PII Cycle Prediction](https://github.com/exercise-reu-2026-stable-matching/pii-state-data-ml). This is the *mean-state-data-collection* branch, which is used to generate data for the mean models for PII Cycle Prediction.

## Usage

The sequential solver runs PII until it has found a specified number of trials that converge and an equal number that cycle. This number as well as the size are passed as the first and second arguments, respectively. The third argument specifies the name of the file with the data. The fourth argument is optional and is used when running the solver on multiple nodes at the same time.
```java
Random rng = new Random();
final int nIters       = Integer.parseInt(args[0]); // Number of convergent trials and cycle trials to get
final int nSize        = Integer.parseInt(args[1]); // Size of the preference matrix
final String writeFile = args[2]; // File to write the data to

final int programIndex = args.length >= 4 ? Integer.parseInt(args[3]) : 0; // Optional program index argument when running on multiple nodes

PII pii = new PII(); // Can pass rng set seed, i.e. PII(rng)
```

For example, to generate your own data for n = 10 for 1000 trials that converge and 1000 that cycle, compile *Main.java* and run the following command. This will save the data to *stateData_2000_10.csv*. This file can then be moved to *mean_models/mean_data* in PII Cycle Prediction.
```bash
java Main 1000 10 stateData_2000_10
```

The resulting data file will contain data about the program number, the trial number, the counts of pair types and NM2 graph components, the mean lists, and whether the trial converged or cycled.
## File Manifest

- Main.java - File to be run to collect mean state data
- StateData.java - File that contains the StateData object used to collect data that is written to the csv
- PII.java - File that runs the PII algorithm and adds data to a StateData object

## Contact
Matthew Goldman
mgoldman5@binghamton.edu

Juniper Pasternak
juniper.pasternak23@kzoo.edu
## Acknowledgements

Thank you to William Bradley and Jeffery Xu for their Java sequential solver implementation of PII.

## License

[MIT](https://choosealicense.com/licenses/mit/)