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

(def *tournament-size* 3)

(defn generate
  "Function to generate a new population with a given size,
   crossover rate, elitisim rate and mutation rate."
  [size crossover elitism mutation]
  (let [p (sort-by (fn [x] (:fitness x)) (repeatedly size chromosome/generate))]
    (hash-map :crossover crossover
	          :elitism elitism
	          :mutation mutation
	          :population (vec p))))

(defn- best-fitness
  "Function used to retrieve the better of two provided genes based on
   their fitness."
  [v1 v2]
  (if (< (:fitness v1) (:fitness v2)) v1 v2))

(defn- tournament-selection
  "Function to perform a tournament selection to retrieve a random
   chromosome from the given sequence of chromosomes."
  [col]
  (let [tournament-size (int *tournament-size*)]
    (loop [best (rand-nth col) i (int 0)]
      (if (== i *tournament-size*)
	    best
	    (recur (best-fitness best (rand-nth col)) (inc i))))))

(defn evolve
  "Function to evolve a given population.  The population carries over
   a portion of the original population unchanged, based on the
   :elitism ratio defined for the population.  The remainer of the
   population is comprised of children being mated with random
   parents (based on the :crossover ratio), or randomly selected
   from the population.  Any non-elitist members are subject to
   random mutation (based on the :mutation property)."
  [p]
  (let [chromosomes (:population p)
        size (int (count chromosomes))
	    elitism-size (int (Math/round (* (:elitism p) size)))
	    r-mutate (fn [x] (if (<= (rand) (:mutation p)) (chromosome/mutate x) x))]
    (loop [buffer (subvec chromosomes 0 (inc elitism-size))
	       idx (int (inc elitism-size))]
      (if (>= idx size)
	    (assoc p :population (vec (sort-by #(:fitness %) (take size buffer))))
	    (if (<= (rand) (:crossover p))
	      ;; Perform a crossover
	      (let [c1 (tournament-selection chromosomes)
		        c2 (tournament-selection chromosomes)]
	        (recur (into buffer (doall (map #(r-mutate %) (chromosome/mate c1 c2))))
		           (inc (inc idx))))
	      ;; Perform a straight copy with mutation
	      (recur (conj buffer (r-mutate (get chromosomes idx)))
		         (inc idx)))))))