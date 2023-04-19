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
