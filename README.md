# Java Sequential Solver - CPII Data Collection

The Java Sequential Solver contains a sequential implementation of the CPII algorithm. This is used as an input data generator that is used in conjunction with [CPII Variant Selection](https://github.com/exercise-reu-2026-stable-matching/cpii-ml). This is the *cpii-data-collection* branch, which is used to generate data for CPII Variant Selection.

## Usage

The sequential solver runs both variants of CPII for a specified number of trials. The size and the number of trials are passed as the first and second arguments, respectively. The third argument specifies the name of the file with the data. The fourth argument is optional and is used when running the solver on multiple nodes at the same time.
```java
Random rng = new Random();

final int nSize  = Integer.parseInt(args[0]); // Size of the preference matrix
final int nTrials = Integer.parseInt(args[1]); // Number of CPII trials to run
final String writeFile = args[2]; // File to write the data to
final int programIndex = args.length >= 4 ? Integer.parseInt(args[3]) : 0; // Optional program index argument when running on multiple nodes

final Random rng = new Random(programIndex); // Random seed is set to program index to prevent duplicate trials on different nodes
final int maxStateBuffer = 100000; // Specifies the amount of StateData objects to write to the csv at a time
```

For example, to generate your own data for n = 10 for 1000 trials of CPII, compile *Main.java* and run the following command. This will save the data to *data_10_1000.csv*. This file can then be moved to *data/* in CPII Variant Selection. Note: The *data/* directory may needed to be created first.
```bash
java Main 10 1000 data_10_1000
```

There will be three resulting data files. The first will use an _trial suffix. This file contains data about the program and trial indices, as well as the number of iterations each variant took. This file also contains the left and right values for each entry in the preference matrix. The second file uses an _iterMF suffix. This file contains data about the program, trial, and iteration indices, as well as the flattened indices of various pair types when running CPII on male-female-dominant pair selection. The third file uses an _iterFM suffix. This file also contains data about the program, trial, and iteration indices, as well as the flattened indices of various pair types when running CPII on female-male-dominant pair selection. These three files are joined during execution of a CPII model based on the program and trial indices.
## File Manifest

- Main.java - File to be run to collect CPII input data, also handles adding data to the TrialStateData object
- TrialStateData.java - File that contains the TrialStateData object used to collect data that is written to the trial csv
- IterationStateData.java - File that contains the IterationStateData object used to collect data that is written to the two iteration csvs
- CPIIDataCollection.java - File that inherits from CPIIFast and handles adding data to the two IterationStateData objects
- CPIIFast.java - File that inherits from CPII that runs a more efficient version of the CPII algorithm
- CPII.java - File that contains a sequential implementation of the CPII algorithm

## Contact
Matthew Goldman
mgoldman5@binghamton.edu

Juniper Pasternak
juniper.pasternak23@kzoo.edu
## Acknowledgements

Thank you to William Bradley and Jeffrey Xu for their Java sequential solver implementation of CPII.

## License

[MIT](https://choosealicense.com/licenses/mit/)