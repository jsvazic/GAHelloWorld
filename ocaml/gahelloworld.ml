(** Genetic algorithms.

    This module defines a {!CHROMOSOME} module type to represent arbitrary genomes,
    an {!Algorithm} functor which allows running a genetic algorithm on that
    genome, and a {!LazyFitness} utility functor for allowing memoization of
    the fitness function.

    @author Victor Nicollet
*)

(* Copyright (C) 2011 by Victor Nicollet

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
*)

(** A genome, represented as a generic module type. *)
module type CHROMOSOME = sig

  (** What a genome is. Expected to be immutable. *)
  type t 

  (** How to combine two genomes as a crossover *)
  val mate : t -> t -> t

  (** Mutate a single gene in a genome. *)
  val mutate : t -> t

  (** Create a random genome. *)
  val random : unit -> t

  (** The fitness of a genome. The smaller, the better. Zero means success. *)
  val fitness : t -> int

end

(** A lazy-evaluation wrapper for genomes: this prevents the fitness
    from being re-computed on every call. 
*)
module LazyFitness = functor(Chromosome:CHROMOSOME) -> ( struct
    
  type t = { genome : Chromosome.t ; fitness : int Lazy.t }

  let wrap inner = { genome = inner ; fitness = lazy (Chromosome.fitness inner) }

  let mate g1 g2 = wrap (Chromosome.mate g1.genome g2.genome)

  let mutate g = wrap (Chromosome.mutate g.genome)

  let random () = wrap (Chromosome.random ())

  let fitness g = Lazy.force g.fitness

  let value g = g.genome

end : sig

  include CHROMOSOME

  (** Return the wrapped value. *)
  val value : t -> Chromosome.t

end )

(** Utility function for comparing two values by applying a function *)
let by f a b = compare (f a) (f b)

(** Running an algorithm on a given kind genome. *)
module Algorithm = functor(Chromosome:CHROMOSOME) -> struct      

  (** Run the algorithm. Returns the list of the best elements in each generation.
      @param tsize Tournament size 
      @param psize Population size
      @param crossover Crossover probability (0-1)
      @param elitism The top elitism% of the population is kept on each generation.
      @param mutation Mutation probability (0-1)
      @param generations Maximum number of generations allowed
  *)
  let run ~tsize ~psize ~crossover ~elitism ~mutation ~generations () =     

    let elitism_bound = int_of_float (float_of_int psize *. elitism) in

    (* Select a random subset of the population, then keep the genome
       with the highest fitness in that subset. [tsize] is the size of
       the tournament. This function relies on the fact that the
       smaller the index of a genome in the population, the better
       its fitness.
    *)
    let tselect pop = 
      let idx = Array.init (tsize-1) (fun _ -> Random.int psize) in
      pop.(Array.fold_left min (Random.int psize) idx)
    in

    let init f = 
      let array = Array.init psize f in
      Array.sort (by Chromosome.fitness) array ; array
    in

    (* Initial population based on random genomes *)
    let initial = init (fun _ -> Chromosome.random ()) in

    (* Evolve a new population based on the previous one *)
    let evolve pop = 
      init begin fun i -> 

	  let gene = 
	    if i >= elitism_bound && Random.float 1.0 <= crossover
	    then Chromosome.mate (tselect pop) (tselect pop)
	    else pop.(i)
	  in

	  if i >= elitism_bound && Random.float 1.0 <= mutation
	  then Chromosome.mutate gene
	  else gene
	    
      end in

    (* Loop until we run out of generations or the best genome in the population
       has solved the task. Return the best element of each generation.
    *)
    let rec loop generations pop = 
      if generations = 0 then [] else 
	if Chromosome.fitness pop.(0) = 0 then [pop.(0)] else
	  pop.(0) :: loop (generations - 1) (evolve pop)
    in

    loop generations initial

end

(**/**)

(* Running our hello world algorithm ------------------------------------------- *)

(* "Hello World" genomes are strings of the same length as the target. 
   
   While not immutable (because they are strings), genomes are never 
   modified by the functions inside.
*)
module HelloWorld = struct

  type t = string

  let target = "Hello, world!"
  let length = String.length target

  let randchar () = Char.chr (Random.int (121-32) + 32)

  let mate g1 g2 = 
    let n = Random.int length in
    String.sub g1 0 n ^ String.sub g2 n (length - n)

  let mutate g = 
    (* Create a copy to avoid mutation. *)
    let g' = String.copy g in
    g'.[Random.int length] <- randchar () ; g'

  let random () = 
    let g = String.create length in
    for i = 0 to length - 1 do 
      g.[i] <- randchar ()
    done ; g

  (* Fitness is the number of characters that do not match the target. *)
  let fitness g = 
    let d = ref 0 in
    for i = 0 to length - 1 do
      d := !d + abs (Char.code g.[i] - Char.code target.[i])
    done ; !d

end

module MyChromosome     = LazyFitness(HelloWorld)
module MyAlgorithm  = Algorithm(MyChromosome)

let algorithm = MyAlgorithm.run 
  ~tsize:3
  ~psize:2048
  ~crossover:0.8
  ~elitism:0.1
  ~mutation:0.3
  ~generations:16384

let results = 
  List.iter print_endline
    (List.map MyChromosome.value (algorithm ()))

(* A few unit tests --------------------------------------------- *)

let test_fitness = 
  assert (HelloWorld.fitness "Hello, world!" = 0) ;
  assert (HelloWorld.fitness "H5p&J;!l<X\\7l" = 399) ;
  assert (HelloWorld.fitness "Vc;fx#QRP8V\\$" = 297) ;
  assert (HelloWorld.fitness "t\\O`E_Jx$n=NF" = 415)

let test_gen_random = 
  for i = 0 to 1000 do
    let c = HelloWorld.random () in
    assert (HelloWorld.fitness c >= 0) ;
    assert (String.length c = 13) ;
    String.iter (fun c -> 
      assert (Char.code c >= 32) ;
      assert (Char.code c <= 121) 
    ) c
  done

let test_mate = 
  for i = 0 to 1000 do
    let c1 = HelloWorld.random () in
    let c2 = HelloWorld.random () in
    let c' = HelloWorld.mate c1 c2 in
    assert (HelloWorld.fitness c' >= 0) ;
    assert (String.length c' = 13) 
  done
  
