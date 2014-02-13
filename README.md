# About

S-Match is a framework for semantic matching algorithms.
To learn more about S-Match visit: [semanticmatching.org](http://semanticmatching.org/)

# Getting started

S-Match is written in Java and runs in a Java Virtual Machine version 6 or later.
You can get started by [downloading S-Match release](http://sourceforge.net/projects/s-match/files/),
building [S-Match binary release](https://github.com/s-match/s-match-utils/),
or by [downloading S-Match examples](https://github.com/s-match/s-match-examples/).

## Command line

You can run S-Match command-line interface: ```bin/match-manager``` or ```bin\match-manager.bat```.

You can run the matching of provided sample trees by running ```bin\all-cw.bat``` or 
```bin/all-cw.sh```. This will execute the following steps:
 1. convert sample trees in ```test-data/cw/c.txt``` and ```test-data/cw/w.txt``` from text files to internal XML format.
 2. enrich the trees with logical formulas ("offline" steps of the algorithm)
 3. match the trees ("online" steps of the algorithm)
 4. match the trees with the minimal matching algorithm
        
The script will render the results of the
 * step c) into: ```test-data/cw/result-cw.txt```
 * step d) into: ```test-data/cw/result-minimal-cw.txt```

## GUI

Run ```bin\s-match-gui.bat``` or ```bin/s-match-gui```.

## Examples

[S-Match Examples](https://github.com/s-match/s-match-examples/) demonstrates the use of S-Match API.

## Documentation

S-Match is accompanied by [S-Match Javadocs](http://semanticmatching.org/javadocs/) and [S-Match Wiki](https://github.com/s-match/s-match-core/wiki/).
