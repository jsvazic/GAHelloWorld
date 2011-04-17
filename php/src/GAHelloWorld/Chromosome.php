<?php
# The MIT License
#
# Copyright (c) 2011 Carlos André Ferrari
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

namespace GAHelloWorld;

/**
 * A php script that demonstrates a simple "Hello, world!" application using
 * genetic algorithms.
 *
 * @author Carlos André Ferrari
 */
class Chromosome {

    public $fitness = 999;
    public $gene = '';
    public $population;

    /*
     * This class is used to define a chromosome for the gentic algorithm
     * simulation.

     * This class is essentially nothing more than a container for the details
     * of the chromosome, namely the gene (the string that represents our
     * target string) and the fitness (how close the gene is to the target
     * string).

     * Note that this class is immutable.  Calling mate() or mutate() will
     * result in a new chromosome instance being created.

     */
    public function __construct($population, $gene)
    {
        $this->population = $population;
        $this->gene = $gene;
        $this->fitness = $this->calculateFitness($gene);
    }

    /*
     * Method used to mate the chromosome with another chromosome,
     * resulting in a new chromosome being returned.
     */
    public function mate(Chromosome $mate)
    {
        $pivot = rand(0, strlen($this->gene)-2);
        
        $length = strlen($mate->gene);

        $gene1 = substr($this->gene, 0, $pivot) . substr($mate->gene, -$length+$pivot);
        $gene2 = substr($mate->gene, 0, $pivot) . substr($this->gene, -$length+$pivot);

        return array(
            new self($this->population, $gene1),
            new self($this->population, $gene2)
        );
    }

    /*
     * Method used to generate a new chromosome based on a change in a
     * random character in the gene of this chromosome.  A new chromosome
     * will be created, but this original will not be affected.
     */
    public function mutate()
    {
        $gene = $this->gene;
        $gene{rand(0, strlen($gene)-1)} = chr(rand(32, 122));

        return new self($this->population, $gene);
    }

    /*
     * Helper method used to return the fitness for the chromosome based
     * on its gene.
     */
    public function calculateFitness($gene)
    {
        $fitness = 0;
        foreach (str_split($gene) as $k => $ch)
            $fitness += abs( ord($ch) - ord($this->population->targetGene{$k}) );
        
        return $fitness;
    }

    /*
     * A convenience method for generating a random chromosome with a random
     * gene.
     */
    public static function genRandom($population)
    {
        $i = strlen($population->targetGene);
        $chars = range(' ', 'z');
        shuffle($chars);
        $output = substr(implode($chars), 0, $i);
        $chromosome = new self($population, $output);
        return $chromosome;
    }

    /*
     * return the fitness of the gene,
     * its uset to sort the population
     */
    public function __toString(){
        return (string)$this->fitness;
    }
    
}
