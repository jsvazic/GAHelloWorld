;; The MIT License
;; 
;; Copyright (c) 2011 John Svazic
;; 
;; Permission is hereby granted, free of charge, to any person obtaining a copy
;; of this software and associated documentation files (the "Software"), to deal
;; in the Software without restriction, including without limitation the rights
;; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;; copies of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:
;; 
;; The above copyright notice and this permission notice shall be included in
;; all copies or substantial portions of the Software.
;; 
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
;; THE SOFTWARE.

(ns net.auxesia.chromosome
  "This namespace defines functions used for generating, interrogating and
   evaluating chromosomes."
  (:gen-class))

;; The target string that we'll compare against for calculating fitness
(def *target-gene* (vec "Hello, world!"))

(defrecord Chromosome [gene fitness])

(defn- fitness
  "A function used to calculate the fitness of a given string, where
   the fitness is determined to be the absolute sum of the difference
   in the ascii characters for the string compared with the target
   string."
  [v]
  (reduce + (map #(Math/abs (- (int %) (int %2))) v *target-gene*)))

(defn- rand-gene
  "A convenience function that can generate a random gene for a given length."
  [len]
  (vec (repeatedly len (fn [] (char (+ 32 (rand-int 90)))))))
	  
(defn mutate
  "Function used to mutate a given chromosome.  A new chromosome is created
   and returned, where a single element of the :gene in the given chromosome
   is modified and a new :fitness is assigned based on the new :gene."
  [c]
  (let [old-gene (:gene c)
	    idx (rand-int (count old-gene))
	    new-gene (assoc old-gene idx (char (mod (+ (int (get old-gene idx)) (+ 32 (rand-int 90))) 122)))]
    (Chromosome. new-gene (fitness new-gene))))

(defn generate
  "Function to generate a new chromosome, either with a random gene or
   a specific gene."
  ([]
     (generate (rand-gene (count *target-gene*))))
  ([gene]
     (Chromosome. gene (fitness gene))))
	 
(defn mate
  "Function used to mate two chromosomes with each other.  The mating
   process will return a new vector of two children, representing
   the mating of the two given chromosomes."
  [c1 c2]
  (let [pivot (rand-int (count *target-gene*))
	    child1 (into (subvec (:gene c1) 0 pivot) (subvec (:gene c2) pivot))
	    child2 (into (subvec (:gene c2) 0 pivot) (subvec (:gene c1) pivot))]
    (vector (generate child1) (generate child2))))