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
The library could be installed via Jitpack, so first you need to add Jitpack to your Gradle dependencies repositories:
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
### Instantiating the models

There are 6 classes used to describe a structure:
- Node: a defined point where loads can be applied and where beams and supports can be based on;
- Support: a structure support, with unknown reaction loads to be calculated;
- Beam: the library's object of analysis. It's used to determine the plot orientations, and it's defined by two nodes, at the ends;
- Point Load: a load vector applied in a given node;
- Distributed Load: a load vector distributed in a given line (constrained by two node);
- Structure: used to hold all the nodes and general information;

Note that: bending moment loads are represented in the system as scalar values in a given node.

By the way that the library is defined now, a structure holds a list of node and the node holds all the loads and supports. This implies that:
- First instantiate the `Structure`;
- Then instantiate the `Nodes` and add then to the `Structure` (could be passed in the `Structure` constructor, in a `hashSet`);
- Finally, in each node add the loads, beams and supports;

#### Code sample
The simplest way to declare a structure is:
```kotlin
val sampleStructure = Structure(
    name = "My sample structure",
    nodes = hashSetOf(
        Node(name = "A", pos = Vector(x = 0, y = 0)),
        Node("B", Vector(0, 1)),
        Node("C", Vector(0, 2))
    )
)
Beam(node1 = sampleStructure["A"], node2 = sampleStructure["C"])
Support(node = sampleStructure["A"], gender = Support.Gender.FIRST, dir = Vector.VERTICAL)
Support(sampleStructure["C"], Support.Gender.SECOND, Vector.VERTICAL)
PointLoad(node = sampleStructure["B"], vector = Vector(0, -30))
DistributedLoad(node1 = sampleStructure["B"], node2 = sampleStructure["C"], vector = Vector(0,-10))
```
`Vector.VERTICAL` is a const default of `Vector(0 ,1)`.

The faster way of declaring the same structure is by using kotlin scope functions:
```kotlin
val sampleStructure = Structure("My sample structure",
    hashSetOf(
        Node("A", Vector(0, 0)).apply { Support(it, Support.Gender.FIRST, Vector.VERTICAL) },
        Node("B", Vector(0, 1)).apply { PointLoad(it, Vector(0, -30)) },
        Node("C", Vector(0, 2)).apply { Support(it, Support.Gender.SECOND, Vector.VERTICAL) }
    )
)
Beam(sampleStructure["A"], sampleStructure["C"])
DistributedLoad(sampleStructure["B"], sampleStructure["C"], Vector(0,-10))
```

If you are using the JVM, the library also have a Yaml parser, to instantiate a `Structure` from String, which could be
useful for basic user input and saving data in files.

As it is, the library uses PT-BR only declarations and has no current support for other languages (good first issue?).

The following example defines most of the syntax for Yaml declarations:
```yaml
---
estrutura: Sample Structure
nós:
  A: [0, 0]  # node name: position vector
  B: 
    x: 0
    y: 1
  C: [0, 2]
apoios:
  A:  # reference to the applied node
    gênero: 1
    direção: vertical
  C:
    gênero: 2
    direção: [0, 1]  # vertical and horizontal are also keywords for vectors
barras:
  - [A, C]  # this connects node A to node C
cargas:
  F1:  # random unused name
    nó: B  # applied node
    direção: vertical
    módulo: -30
  F2:
    nós: [B, C]  # distributed load between node B and C
    vetor: [0, -10]  # vetor or (direção and módulo) are two equivalent syntax for declaring a load
...
```

### 2. Using the `StructureSolver`

The `StructureSolver` class is made to facilitate the calculations and plotting routines, simplifying the entire process to those given steps.
All its public fields and methods can be used. For more information read the source code's documentation.

### 3. Making the diagrams
The diagrams are generated completely by the `Diagrams.getDiagram()`. Just need to pass the analyzed `Structure`,
the focused `Beam`, the diagram function to be plotted and the step size (related to the horizontal resolution,
the smaller the step, the more points will be plotted). The diagram function should be passed as a reference to one of
those functions: `generateNormalFunction`, `generateShearFunction` and `generateMomentFunction`.

Example: generating the Shear Stress Diagram for a given `structure`, `beam` with step of 0.01: 
`Diagrams.getDiagrams(structure, beam, generateShearFunction, 0.01F)`

The method returns a pair: the first item is a pair of lists, representing the x and y points of the result plot;
the second item is a list of `Ploynomial`s that where used to generate the x and y values.

## Contribute
For any changes, an issue is first required.

## See also
- [Estabilidade-IO](https://github.com/icarob-eng/Estabilidade-IO/), a Android client implementation for Kstability.
