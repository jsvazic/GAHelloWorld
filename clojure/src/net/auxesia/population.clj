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
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
;; THE SOFTWARE.

(ns net.auxesia.population
  "This namespace defines functions used to generate, interrogate,
   and evolve a genetic algoritm population."
  (:require [net.auxesia.chromosome :as chromosome])
  (:gen-class))

(def *tournament-size* 64)

(defn generate
  "Function to generate a new population with a given size,
   crossover rate, elitisim rate and mutation rate."
  [size crossover elitism mutation]
  (let [chromosomes (sort-by
		     (fn [x] (:fitness x))
		     (vec (repeatedly size chromosome/generate)))]
    (hash-map :crossover crossover
	      :elitism elitism
	      :mutation mutation
	      :population chromosomes)))

(defn best
  "Function to retrieve the best fit chromosome for
   a population."
  [p]
  (first (:population p)))

(defn- tournament-selection [seq]
  "Function to perform a tournament selection to retrieve a random
   chromosome from the given sequence of chromosomes."
  (let [pop-size (count seq)]
    (loop [best (nth seq (rand-int pop-size))
	   i 0]
      (if (= i *tournament-size*)
	best
	(let [contender (nth seq (rand-int pop-size))
	      new-best (if (<= (:fitness best) (:fitness contender))
			  best
			  contender)]
	  (recur new-best (inc i)))))))

(defn evolve
  "Function to evolve a given population.  The population carries over
   a portion of the original population unchanged, based on the
   :elitism ratio defined for the population.  The remainer of the
   population is comprised of children being mated with random
   parents (based on the :crossover ratio), or randomly selected
   from the population.  Any non-elitist members are subject to
   random mutation (based on the :mutation property)."
  [population]
  (let [chromosomes (:population population)
        size (count chromosomes)
	elitism-size (int (Math/round (* (:elitism population) size)))
	r-mutate (fn [x] (if (<= (rand) (:mutation population))
			   (chromosome/mutate x)
			     x))]
    (loop [buffer (take elitism-size chromosomes)
	   idx (inc elitism-size)]
      (if (>= idx size)
	(assoc population :population
	       (vec (sort-by (fn [x] (:fitness x)) (doall (take size buffer)))))
	(if (<= (rand) (:crossover population))
	  ;; Perform a crossover
	  (let [c1 (tournament-selection chromosomes)
		c2 (tournament-selection chromosomes)]
	    (recur (into buffer (map #(r-mutate %) (chromosome/mate c1 c2)))
		   (inc (inc idx))))
	  ;; Perform a straight copy with mutation
	  (recur (conj buffer (r-mutate (nth chromosomes idx)))
		 (inc idx)))))))