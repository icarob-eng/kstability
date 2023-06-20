# kstability
A Kotlin library for finding reaction forces and charts related to Euler-Bernoulli beams. It's features are:
- Data classes for representing 2D structures, including loads, supports and beams. At the moment, only single beam structures are supported,
but in the future, the system will work with trusses and more complex structures;
- Finding the reaction loads in the given supports (with a restricted number of support arrangements);
- Finding Macaulay's functions for a given beam's bending moment and shear and normal stress;
- An defined ploting interface that should be override to make the system automatcally plot the Macaulays's functions (which also can be done manually);

The algorithm explanations are shown in the [calculation log (pt-Br)](https://github.com/icarob-eng/kstability/blob/main/memoria_de_calculo.md).

**For now the library isn't fully tested, so use it at your own risk.**

## Installation
The library isn't yet publish and it's in experimental fase, so the only installation method is cloning this repository.

```bash
$ git clone https://github.com/icarob-eng/kstability.git
```

## Usage
There are three steps to using this library:
1. Implmenting the [ploting interface](https://github.com/icarob-eng/kstability/blob/main/src/commonMain/kotlin/com/kstabilty/IStructureDrawer.kt);
2. Instantiating objects, decribing the structure;
3. Putting all together in an [`ChartingRoutines`](https://github.com/icarob-eng/kstability/blob/main/src/commonMain/kotlin/com/kstabilty/ChartingRoutines.kt) instance.

### 1. Instanciating the models

There are 6 classes used to describe a structure:
- Node: a defined point where loads can be applied and beams and supports can be based on;
- Support: a structure support, with unknown reaction loads to be calculated;
- Beam: the library's object of analysis. It's used to determine the plot orientations and it's defined by two node, at the ends;
- Point Load: a load vector applied in a given node;
- Distributted Load: a load vector distributed in a given line (constrained by two node);
- Structure: used to hold all other object informations;

Note that: bending moment loads are represented in the system as scalar values in a given node.

By the way that the library is defined, a structure holds a list of node and the node holds all the loads and supports. This implies that:
- First instanciate the structure;
- Then instanciate the node, passing the strucure (else the structure wouldn't kwon about the node);
- Finally in each node add the loads;

See also the class documentation for the [creators](https://github.com/icarob-eng/kstability/blob/main/src/jvmMain/kotlin/com/moon/kstability/CreatorMenagers.kt). They serve to instantiate a structure from a string formated in yaml and only orks at jvm.

### 2. Using the `StructureSolver`

The `StructureSolver` class is made to facilitate the calculations and ploting routines, simplifing the entire process to those given steps.
All it's public filds and methods can be used. For more information read the source code's documentation.

### 3. Making the diagrams
The diagrams are generated completely by the `Diagrams.getDiagram()`. Just need to pass the analized `Structure`, the foccused `Beam`, the the diagram function to be ploted and the step size (releted to the horizontal resolution, the smaller the step, the more points will be ploted). The diagram function should be passed as a reference to one of those functions: `generateNormalFunction`, `generateShearFunction` and `generateMomentFunction`.

Example: generating the Shear Stress Diagram for a giveng `structure`, `beam` with step of 0.01: `Diagrams.getDiagrams(structure, beam, generateShearFunction, 0.01F)`

The method returns a pair of lists, representing the x and y points of the result plot.

## Contribute
For all changes, a issue is required, to discuss what you would like to change.

## License
[GNU General Public License v3.0](https://github.com/icarob-eng/kstability/blob/main/LICENSE)
