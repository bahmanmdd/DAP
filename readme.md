# A Stochastic Approximation Approach to Spatio-Temporal Anchorage Planning with Multiple Objectives - Simulation Code

This repository contains the Java simulation code for the research paper: **"A stochastic approximation approach to spatio-temporal anchorage planning with multiple objectives"

**Authors of the Paper:** Bahman Madadi, Vural Aksakalli

**Journal:** Expert Systems With Applications, Volume 146, 15 May 2020, 113170

**DOI:** [https://doi.org/10.1016/j.eswa.2019.113170](https://doi.org/10.1016/j.eswa.2019.113170)


## Project Overview

The project implements a simulation environment to study and optimize anchorage planning strategies. Vessels arrive and depart dynamically, and the system aims to find optimal berth locations based on a set of planning metrics. The core of the optimization uses the SPSA algorithm to tune the coefficients of these planning metrics to minimize a multi-objective function.

The simulation allows for different scenarios (e.g., Ahirkapi, Busy, Idle, Average traffic conditions) and parameters to be configured, enabling the reproduction of results presented in the research paper and further experimentation.

## Motivation for Project Structure

The project is structured to be self-dependent, primarily relying on Java SDK 1.8. This structure was chosen to ensure the code runs without extensive external library management, making it more portable and easier to execute for reproducing the research findings. While the nesting of files within the `PGraph_Anchorage_Duration` sub-directory might appear complex, it organizes the various components of the simulation, including anchorage geometry, vessel dynamics, arrival/departure generation, policy implementation, and the SPSA optimization logic.

## Project Structure

The project root directory should be named `DAP`. The core simulation code and libraries reside within a subdirectory named `PGraph_Anchorage_Duration`.

```
DAP/                                # Project Root Directory
├── PGraph_Anchorage_Duration/         # Core simulation engine, libraries, and source code
│   └── Pgraph/                        # IntelliJ IDEA project directory for this sub-directory
│       ├── PGraph.iml                 # IntelliJ IDEA project file for the main module
│       ├── lib/                       # Contains necessary library JARs
│       │   └── pgraph/
│       │       └── util/
│       │           └── javaGeom-0.11.1.jar # Geometry library used
│       └── src/                        # Source code
│           ├── META-INF/
│           ├── deneysel/               # Experimental or specific components
│           │   ├── alg/
│           │   ├── experiments/
│           │   └── org/
│           └── pgraph/                 # Core simulation package
│               ├── anchorage/          # Anchorage specific logic
│               │   ├── TimedAnchorageManager.java  <-- MAIN SIMULATION SCRIPT & SCENARIO SELECTION
│               │   ├── TimedAnchorArea.java        <-- KEY PARAMETERS & SPSA LOGIC
│               │   ├── Anchorage.java
│               │   ├── AnchorageConfig.java
│               │   └── ... (other anchorage related classes)
│               ├── anya/
│               ├── base/
│               ├── CAOStar/
│               ├── distributions/      # Probability distributions for arrivals, dwell times, etc.
│               ├── grid/
│               ├── gui/                # Graphical User Interface components (if animation is enabled)
│               ├── intersec...handler/
│               ├── kshortestpath/
│               ├── policy/             # Anchorage policies (e.g., HybridTAPV2)
│               ├── ranga/
│               ├── rdp/
│               ├── specialzone/
│               ├── tag/
│               └── util/               # Utility classes, including geometry helpers
│                   ├── Polygon2D.java
│                   └── ... (other utility classes)
├── results/                        # Simulation RESULTS (CSV statistics files, etc.) will be generated here
├── readme.md                       # This README file
└── DAP.iml                         # IntelliJ IDEA project file for the main DAP project
```

- **Main Simulation Class:** `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/TimedAnchorageManager.java`

- **Key Parameters & SPSA Logic:** `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/TimedAnchorArea.java`

## Key Parameters and Scenarios

The simulation's behavior is primarily controlled by parameters within two main files located inside `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/`:

1.  **`TimedAnchorageManager.java`**:
    * `private static final String SCENARIO`: This crucial variable at the top of the file determines the operational scenario. It can be set to:
        * `"Ahirkapi"`: Simulates the Ahirkapi anchorage as detailed in the paper.
        * `"Busy"`: Simulates a generic busy anchorage.
        * `"Idle"`: Simulates a generic idle anchorage.
        * `"Average"`: Simulates a generic average traffic anchorage.
    * `public final boolean ANIMATION`: Set to `true` to enable a visual animation of the simulation, or `false` to run in headless mode for faster execution. Default in the provided code is `false`.
    * The `main` method calls `test1()`, which configures and runs the simulation based on the selected `SCENARIO`.

2.  **`TimedAnchorArea.java`**: This class contains a large number of constants and variables that define the simulation environment, objectives, and SPSA algorithm parameters. Key parameters include:
    * **Simulation Duration:**
        * `WORMUP_DAYS`: Warm-up period for the simulation.
        * `RUN_DAYS`: Duration of the simulation run after warm-up for collecting statistics.
    * **SPSA and Optimization Parameters:**
        * `spsa` (boolean): Enables or disables the SPSA optimization. If `false`, it might use a predefined `theta` vector (see `myTheta()` method).
        * `NO_OF_ITERATION`: Number of SPSA iterations.
        * `RUN_PER_ITERATION`: Number of simulation replications per SPSA iteration.
        * `NO_OF_RUNS`: Total number of simulation runs.
        * `W1`, `W2`, `W3`: Weights for the multi-objective function (Risk, Utilization, Distance).
        * `lambda`: Regularization parameter for SPSA.
        * SPSA algorithm parameters: `alpha`, `gamma`, `a`, `c`, `A` (defined within `createQvectorSPSA()`).
    * **Anchorage Characteristics:**
        * `ANCHORAGE_LENGTH`: Defined in `TimedAnchorageManager.java` based on the scenario.
        * Area definition (`setArea`), entry side (`setEntrySide`), first entry point (`setFirstEntryPoint`) are set in `TimedAnchorageManager.java`'s `test1()` method based on the scenario.
    * **Statistics and Reporting:**
        * `STATISTICS` (boolean): Enables detailed run-by-run statistics generation.
        * Output files are generated in the `DAP/results/` directory (at the project root). File names include timestamps and parameter settings (e.g., `RunNO_X_CreatTime_YYYYMMDD_hhmmss.csv`, `SPSA_runs_W1-W2-W3_Llambda_timestamp.csv`).

For specific parameter values used to generate results in the paper (e.g., for different scenarios, weightings $W_R, W_U, W_D$), please refer to **Section 7 (Computational Experiments)** and **Tables 3-7** of the research paper.

## Dependencies

* **Java Development Kit (JDK):** Version 1.8 (Java 8).

The project is designed to be self-contained otherwise.

## Setup and Compilation

1.  **Clone the Repository:**
    ```bash
    git clone <repository-url> DAP
    cd DAP
    ```
    (Ensure the `PGraph_Anchorage_Duration` directory is present inside `DAP` as per the repository structure).
2.  **Import into IntelliJ IDEA (Recommended):**
    * Open IntelliJ IDEA.
    * Select "Open" or "Import Project".
    * Navigate to the cloned `DAP` directory and select it. IntelliJ should recognize it as a project and be ready to go.
    * Ensure the project SDK is set to Java 1.8. Go to `File -> Project Structure -> Project -> Project SDK`.
    * IntelliJ IDEA should automatically recognize the project structure, modules, etc.
    * If not in IntelliJ IDEA: 
      * Add `javaGeom-0.11.1.jar` as a library:
      * Go to `File -> Project Structure -> Libraries`.
      * Click the `+` sign and select "Java".
      * Navigate to `DAP/PGraph_Anchorage_Duration/lib/pgraph/util/javaGeom-0.11.1.jar` and add it.
      * Ensure it's added to the correct module (e.g., "PGraph_Anchorage_Duration" or the main "DAP" module).
3.  **Compilation:**
    * IntelliJ IDEA should automatically compile the project. If not, you can trigger a build via `Build -> Build Project`.

## Manual Project Setup (Alternative to using .iml files)

The recommended way to set up this project in IntelliJ IDEA is to import it using the existing `.iml` files (`DAP.iml` and `PGraph.iml`). These files contain project configuration, including source folder definitions and specific folder exclusions that are important for the project to compile and run correctly.

If you are setting up the project manually in IntelliJ IDEA without relying on the `.iml` files, or if you are using a different IDE, you will need to configure the `PGraph_Anchorage_Duration` directory as a source root (or module) and then **manually exclude the following folders** from the `PGraph_Anchorage_Duration/src/` path to avoid compilation errors or incorrect behavior:

* `PGraph_Anchorage_Duration/src/deneysel`
* `PGraph_Anchorage_Duration/src/pgraph/CAOStar`
* `PGraph_Anchorage_Duration/src/pgraph/rdp`

**How to Exclude Folders in IntelliJ IDEA (if setting up manually):**

1.  Open your Project Structure (`File -> Project Structure...`).
2.  Select "Modules" on the left.
3.  Find your module that contains the `PGraph_Anchorage_Duration/src` directory.
4.  In the sources tab for that module, navigate to and select each of the folders listed above.
5.  Click the "Excluded" button (or right-click and mark as "Excluded").


## Running the Simulation

1.  **Navigate to the Main Class:** Open `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/TimedAnchorageManager.java` in IntelliJ IDEA.
2.  **Configure Scenario (Optional):**
    * Modify the `SCENARIO` static final string at the top of `TimedAnchorageManager.java` to choose between `"Ahirkapi"`, `"Busy"`, `"Idle"`, or `"Average"`.
    * Modify the `ANIMATION` boolean flag if you want to see the GUI (slower).
3.  **Configure Parameters (Optional):**
    * To replicate specific experiments from the paper or run new ones, modify the relevant constants in `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/TimedAnchorArea.java` (e.g., `W1`, `W2`, `W3`, `lambda`, `spsa`, `NO_OF_ITERATION`, etc.).
4.  **Run:**
    * Right-click on the `TimedAnchorageManager.java` file in the project explorer or inside the editor.
    * Select "Run 'TimedAnchorageManager.main()'".
5.  **Output:**
    * Console output will show the simulation progress, including replication numbers.
    * Result files (CSV format) will be generated in the `DAP/results/` directory (at the project root). These files contain detailed statistics and objective function values, which can be used to analyze the simulation outcomes.

## Understanding the Code

The core logic resides within the `DAP/PGraph_Anchorage_Duration/src/pgraph/` package.
* **`anchorage/TimedAnchorageManager.java`**: This class orchestrates the simulation.
    * Its `main` method initializes and starts the simulation based on the selected `SCENARIO`.
    * It manages the simulation clock, arrival and departure events, and the main simulation loop.
    * It interacts with `TimedAnchorArea` (for anchorage state), `TimedArrivalGenerator` (for new vessels), and `TimedAnchorPolicy` (for placement decisions).
* **`anchorage/TimedAnchorArea.java`**: This class represents the physical anchorage area and holds most of the simulation logic and parameters.
    * It manages the list of existing anchorages, candidate anchorages, and depth zones.
    * It calculates performance metrics (Area Utilization, Intersection Lengths, Distance Traveled).
    * It contains the SPSA optimization logic (`createQvectorSPSA()`, `updateFinalResults()`, `writeObjectiveFunctionList()`) if the `spsa` flag is true.
    * It handles the generation of output report files.
* **`anchorage/distributions.*`**: Contains classes for generating random variables based on different probability distributions (e.g., `LognormalTAG`, `BetaArrivalGenerator`) for vessel arrivals and service times.
* **`anchorage/policy.*`**: Implements different policies for placing vessels (e.g., `HybridTAPV2`).
* **`util.*`**: Utility classes for geometry (`Polygon2D`, `GeomUtil`), random number generation (`RandUtil`), etc.
* **`gui.*`**: Classes related to the graphical user interface if `ANIMATION` is enabled.

## Reproducing Paper Results

To reproduce the results presented in the research paper:

1.  **Identify the Experiment:** Refer to Section 7 (Computational Experiments) of the paper to identify the specific scenario (Ahirkapi, Busy, Idle, Average) and objective weights ($W_R, W_U, W_D$) for the results you wish to reproduce (Tables 3-6).
2.  **Set Scenario:** Modify the `SCENARIO` variable in `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/TimedAnchorageManager.java`.
3.  **Set Objective Weights & SPSA Parameters:** Modify the `W1`, `W2`, `W3` and other SPSA-related parameters (like `lambda`, `spsa`, `NO_OF_ITERATION`, `RUN_PER_ITERATION`) in `DAP/PGraph_Anchorage_Duration/src/pgraph/anchorage/TimedAnchorArea.java`.
    * The mapping is:
        * Paper $W_R$ (Risk) -> Code `W1`
        * Paper $W_U$ (Utilization, as $1-U$) -> Code `W2`
        * Paper $W_D$ (Distance) -> Code `W3`
    * For example, if the paper uses $W_R=5, W_U=0, W_D=1$, you would set `W1=5`, `W2=0`, `W3=1`.
4.  **Run the Simulation:** Execute `TimedAnchorageManager.java`.
5.  **Analyze Results:** Compare the output CSV files in the `DAP/results/` directory with the values reported in the paper. Due to the stochastic nature of the simulation and SPSA, exact matches might not be possible, but average values over multiple runs should align.

The paper's Table 7 shows "Optimal planning metric coefficients ($\theta$ vector / `qVector` in code) for the Ahırkapı Anchorage." If `spsa` is set to `false` in `TimedAnchorArea.java`, the `myTheta()` method might be used, which could be pre-populated with these optimal values for direct policy evaluation. If `spsa` is `true`, the simulation will attempt to find these coefficients.

### `.gitignore` Recommendations

It is highly recommended to create a `.gitignore` file in the root of your project (`DAP/`) to exclude unnecessary files from version control (see recommended content at the end of this readme).


## Acknowledgements

The development of the dynamic simulation capabilities and the SPSA optimization framework presented in this project builds upon foundational work on an earlier static anchorage simulation model. We gratefully acknowledge Dr. Dindar Öz for developing the initial static simulation, which provided a valuable starting point and insights for this research.


## Contributing

Contributions to this research code are welcome. Please feel free to fork the repository, make improvements, and submit pull requests. You can also open issues for bugs or feature suggestions.


## Citation

If you use this code or the concepts from the associated research in your work, please cite the following paper:

Madadi, B., & Aksakalli, V. (2020). A stochastic approximation approach to spatio-temporal anchorage planning with multiple objectives. *Expert Systems With Applications*, *146*, 113170. [https://doi.org/10.1016/j.eswa.2019.113170](https://doi.org/10.1016/j.eswa.2019.113170)

**BibTeX:**
```bibtex
@article{Madadi2020,
  title = {A stochastic approximation approach to spatio-temporal anchorage planning with multiple objectives},
  author = {Madadi, Bahman and Aksakalli, Vural},
  journal = {Expert Systems With Applications},
  volume = {146},
  year = {2020},
  doi = {10.1016/j.eswa.2019.113170},
  url = {[https://doi.org/10.1016/j.eswa.2019.113170](https://doi.org/10.1016/j.eswa.2019.113170)}
}
```



### `.gitignore` Recommendation (detailed content)

```
.idea/workspace.xml
.idea/shelf/
.idea/tasks.xml
.idea/vcs.xml
.idea/misc.xml
.idea/encodings.xml
.idea/compiler.xml
.idea/jarRepositories.xml
.idea/gradle.xml
.idea/libraries/

# Compiled output from the simulation engine module
PGraph...Duration/out/
PGraph...Duration/build/
PGraph...Duration/target/

# Simulation results (generated by runs into DAP/results/)
**/results/

# General build artifacts
build/
target/
dist/

# Log files
*.log
logs/
*.log.*

# Temporary files
*.tmp
*.temp
*.swp
*~
*.bak

# OS-generated files
.DS_Store
Thumbs.db

# Compressed files
*.zip
*.tar
*.gz
*.rar
```