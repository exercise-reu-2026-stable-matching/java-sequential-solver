# Java Sequential Solver - Matrix State Data Collection

The Java Sequential Solver contains a sequential implementation of the PII algorithm. This is used as an input data generator that is used in conjunction with [PII Cycle Prediction](https://github.com/exercise-reu-2026-stable-matching/pii-ml). This is the *matrix-state-data-collection* branch, which is used to generate data for the matrix models for PII Cycle Prediction.

## Usage

The sequential solver runs PII until it has found a specified number of trials that converge and an equal number that cycle. This number as well as the size are passed as the first and second arguments, respectively. The third argument specifies the name of the file with the data. The fourth argument is optional and is used when running the solver on multiple nodes at the same time.
```java
Random rng = new Random();
final int nIters       = Integer.parseInt(args[0]); // Number of convergent trials and cycle trials to get
final int nSize        = Integer.parseInt(args[1]); // Size of the preference matrix
final String writeFile = args[2]; // File to write the data to

final int programIndex = args.length >= 4 ? Integer.parseInt(args[3]) : 0; // Optional program index argument when running on multiple nodes

final int maxStateBuffer = 100000; // Specifies the amount of StateData objects to write to the csv at a time

PII pii = new PII(); // Can pass rng set seed, i.e. PII(rng)
```

For example, to generate your own data for n = 10 for 1000 trials that converge and 1000 that cycle, compile *Main.java* and run the following command. This will save the data to *stateData_2000_10.csv*. This file can then be moved to *matrix_models/matrix_data* in PII Cycle Prediction.
```bash
java Main 1000 10 matrixStateData_2000_10
```

There will be two resulting data files. The first will use an _iter suffix. This file contains data about the program, trial, and iteration indices. During each iteration, it also tracks the singleton data from mean_state_data and the flattented coordinates of the PII pair types. The second file uses a _trial suffix. This file contains data about the program and trial indices, as well as the left and right values for each entry in the preference matrix. These files are joined during execution of a PII matrix model based on the program and trial indices.
## File Manifest

- Main.java - File to be run to collect matrix state data
- TrialStateData.java - File that contains the TrialStateData object used to collect data that is written to the trial csv
- IterationStateData.java - File that contains the IterationStateData object used to collect data that is written to the iteration csv
- PII.java - File that runs the PII algorithm and adds data to the TrialStateData and IterationStateData objects

## Contact
Matthew Goldman
mgoldman5@binghamton.edu

Juniper Pasternak
juniper.pasternak23@kzoo.edu
## Acknowledgements

Thank you to William Bradley and Jeffrey Xu for their Java sequential solver implementation of PII.

## License

[MIT](https://choosealicense.com/licenses/mit/)
