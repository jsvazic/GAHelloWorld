# Genetic Algorithm Hello World!

This is a simple project intended to showcase genetic algorithms with a well 
known example for all new developers; namely the classic "Hello, world!" 
example!

## Overview

The application simply "evolves" the string "Hello, world!" from a population 
of random strings.  It is intended to be a gentle introduction into the world
of genetic algorithms, using Java, Clojure, Scala and Python.  The programs 
themselves are really quite simple, and more complex topics like crossover 
selection using roulette wheel algorithms, insertion/deletion mutation, etc, 
have not been included.

### History

I've been working with Genetic Algorithms for a little while now and I
stubmled across a 
[C++ implemetation](http://www.generation5.org/content/2003/gahelloworld.asp) 
a while ago.  I decided to bring it back to life and migrate it to Java with 
my own enhancements.  This is far from ideal code, but it was designed to be 
a gentle introduction for newcomers to genetic algorithms.

### But why the <i>net.auxesia</i> package/namespace?

[Auxesia](http://www.theoi.com/Ouranios/HoraAuxesia.html) is the greek
goddess of spring growth, so when dealing with evolutionary programming like
genetic algorithms, the name just seemed to fit.  That and I was trying to be
witty with my naming, and [Dalek](http://en.wikipedia.org/wiki/Dalek) just 
didn't seem right.

## Architecture

The overall architecture for each language is the same.  The genetic algorithm
is broken up between two logical units: a <i>Chromsome</i> and a 
<i>Population</i>.  In some cases a separate driver is also added, but this is
just to keep the logic for the other two components separate and clean.

### Population

The Population has 3 key attributes (a crossover ratio, an
elitism ratio and a mutation ratio), along with a collection of Chromosome 
instances, up to a pre-defined population size.  There is also an evolve() 
function that is used to "evolve" the members of the population.

#### Evolution

The evolution algorithm is simple in that it uses the various ratios during
the evolution process.  First, the elitism ratio is used to copy over a 
certain number of chromosomes unchanges to the new generation.  The remaining
chromosomes are then either mated with other chromosomes in the population, or
copied over directly, depending on the crossover ratio.  In either case, each
of these chromosomes is subject to random mutation, which is based on the 
mutation ration metioned earlier.

The crossover algorithm used for mating is a very basic tournament selection
algorithm.  See 
[Tournament Selection](http://en.wikipedia.org/wiki/Tournament_selection) for
more details.

### Chromosome
Each chromosome has a gene that represents one possible solution to the given 
problem.  In our case, each gene represents a string that strives to match
"Hello, world!".  Each chromosome also has a fitness attribute that is a 
measure of how close the gene is to the target of "Hello, world!".  This 
measurement is just a simple sun of the absolute difference of each character
in the gene to the corresponding character in the target string above.  Each
gene is simply a string of 13 ASCII characters from ASCII 32 to ASCII 121 
inclusive.

The functions operating on Chromsome include <i>mutate()</i> and 
<i>mate()</i>, amongst others as necessary for the various language 
implementations.

#### mutate()
The mutate() function will randomly replace one character in the given gene.

#### mate()
The mate() function will take another chromosome instance and return two new
chromosome instances.  The algorithm is as follows:

1. Select a random pivot point for the genes.
2. For the first child, select the first n < pivot characters from the first 
   parent, then the remaining pivot <= length characters from the second 
   parent.
3. For the second child, repeate the same process, but use the first n < pivot
   characters from the second parent and the remaining characters from the
   first parent.

### Driver

The driver code simply instantiates a new Population instance with a set of
values for the population size, crossover ratio, elitism ratio and mutation
ratio, as well as a maximum number of generations to create before exiting
the simulation, in order to prevent a potential infinite execution.

Depending on the implementation, this code may reside in its own source file.
   
## Usage

Take a look at the README files in [Java](GAHelloWorld/tree/master/java), 
[Clojure](GAHelloWorld/tree/master/clojure),
[Scala](GAHelloWorld/tree/master/scala) and
[Python](GAHelloWorld/tree/master/python) for the specifics for each language.

### Unit tests

Each source implementation has unit tests to go along with the source code.

## Copyright and License

The MIT License

Copyright &copy; 2011 John Svazic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.