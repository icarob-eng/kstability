# kstability
A Kotlin library for finding reaction forces and charts related to Euler-Bernoulli beams. It's features are:
- Data classes for representing 2D structures, including loads, supports and bars. At the moment, only single bar structures are supported,
but in the future, the system will work with trusses and more complex structures;
- Finding the reaction loads in the given supports (with a restricted number of support arrangements);
- Finding Macaulay's functions for a given bar's bending moment and shear and normal stress;
- An defined ploting interface that should be override to make the system automatcally plot the Macaulays's functions (which also can be done manually);

The algorithm explanations are shown in the [calculation log (pt-Br)](https://github.com/icarob-eng/kstability/blob/main/memoria_de_calculo.md).

**For now the library isn't fully tested, so use it at your own risk.**

## Installation
The library isn't ye publish and it's in experimental fase, so the only installation method is cloning this repository.

```bash
$ git clone https://github.com/icarob-eng/kstability.git
```

## Use
There are three steps to using this library:
1. Implmenting the [ploting interface](https://github.com/icarob-eng/kstability/blob/main/src/commonMain/kotlin/com/kstabilty/IStructureDrawer.kt);
2. Instantiating objects, decribing the structure;
3. Putting all together in an [`StructureSolver`](https://github.com/icarob-eng/kstability/blob/main/src/commonMain/kotlin/com/kstabilty/StructureSolver.kt) instance.

### 1. Implementing an `IStructureDrawer`

`IStructureDrawer` is an basic Kotlin interface that defines all ploting methods that should be implemented in order to make the ploting works. It can be implemented with plataform specific code, which makes this library plataform-independent. See the source code for more specifications.

### 2. Instanciating the models

There are 6 classes used to describe a structure:
- Knot: a defined point where loads can be applied and bars and supports can be based on;
- Support: a structure support, with unkwon reaction loads to be calculated;
- Bar: the library's object of analysis. It's used to determine the plot orientations and it's defined by two knots, at the ends;
- Point Load: a load vector applied in a given knot;
- Distributted Load: a load vector distributed in a given line (constrained by two knots);
- Structure: used to hold all other object informations;

Note that: bending moment loads are represented in the system as scalar values in a given knot.

By the way that the library is defined, a structure holds a list of knots and the knots holds all the loads and supports. This implies that:
- First instanciate the structure;
- Then instanciate the knots, passing the strucure (else the structure wouldn't kwon about the knots);
- Finally in each knot add the loads;

### 3. Using the `StructureSolver`

The `StructureSolver` class is made to facilitate the calculations and ploting routines, simplifing the hole process to those given steps.
All it's public filds and methods can be used. For more information read the source code's documentation.
