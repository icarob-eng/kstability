# kstability
![License](https://img.shields.io/github/license/icarob-eng/kstability.svg)
![Release](https://img.shields.io/github/release/icarob-eng/kstability.svg)
[![Jitpack](https://jitpack.io/v/icarob-eng/kstability.svg)](https://jitpack.io/#icarob-eng/kstability)

A Kotlin library for finding reaction forces and charts related to Euler-Bernoulli beams. It's features are:
- Data classes for representing 2D structures, including loads, supports and beams. At the moment, only single beam structures are supported,
but in the future, the system will work with trusses and more complex structures;
- Finding the reaction loads in the given supports (with a restricted number of support arrangements);
- Finding Macaulay's functions for a given beam's bending moment and shear and normal stress;

The algorithm explanations are shown in the [calculation log (pt-Br)](https://github.com/icarob-eng/kstability/blob/main/memoria_de_calculo.md).

## Installation
### Gradle
The library could be installad via Jitpack, so first you need to add Jitpack to your Gradle dependecies repositories:
```groovy
repositories {
  ...
  maven { url 'https://jitpack.io' }
}
```
Then, add the library via the following reference:
```groovy
dependecies {
  ...
  implementation 'com.github.icarob-eng.kstability:kstability:v1.0.1'
}
```
There are 4 possible build targets (only jvm is actually tested):
- `com.github.icarob-eng.kstability:kstability:{version}`
- `com.github.icarob-eng.kstability:kstability-native:{version}`
- `com.github.icarob-eng.kstability:kstability-jvm:{version}`
- `com.github.icarob-eng.kstability:kstability-js:{version}`

### Git
You also can just clone the repository via git:
```bash
$ git clone https://github.com/icarob-eng/kstability.git
```

## Usage
### Instanciating the models

There are 6 classes used to describe a structure:
- Node: a defined point where loads can be applied and beams and supports can be based on;
- Support: a structure support, with unknown reaction loads to be calculated;
- Beam: the library's object of analysis. It's used to determine the plot orientations and it's defined by two node, at the ends;
- Point Load: a load vector applied in a given node;
- Distributted Load: a load vector distributed in a given line (constrained by two node);
- Structure: used to hold all other object informations;

Note that: bending moment loads are represented in the system as scalar values in a given node.

By the way that the library is defined now, a structure holds a list of node and the node holds all the loads and supports. This implies that:
- First instanciate the structure;
- Then instanciate the node, passing the strucure (else the structure wouldn't kwon about the node);
- Finally in each node add the loads;

See also the class documentation for the [creators](https://github.com/icarob-eng/kstability/blob/main/src/jvmMain/kotlin/com/moon/kstability/CreatorMenagers.kt). They serve to instantiate a structure from a string formated in yaml, but only works at jvm.

### 2. Using the `StructureSolver`

The `StructureSolver` class is made to facilitate the calculations and ploting routines, simplifing the entire process to those given steps.
All it's public filds and methods can be used. For more information read the source code's documentation.

### 3. Making the diagrams
The diagrams are generated completely by the `Diagrams.getDiagram()`. Just need to pass the analized `Structure`, the foccused `Beam`, the the diagram function to be ploted and the step size (releted to the horizontal resolution, the smaller the step, the more points will be ploted). The diagram function should be passed as a reference to one of those functions: `generateNormalFunction`, `generateShearFunction` and `generateMomentFunction`.

Example: generating the Shear Stress Diagram for a giveng `structure`, `beam` with step of 0.01: `Diagrams.getDiagrams(structure, beam, generateShearFunction, 0.01F)`

The method returns a pair: the first item is a pair of lists, representing the x and y points of the result plot; the second item is a list of `Ploynomial`s that where used to generate the x and y values.

## Contribute
For any changes, a issue is first required.
