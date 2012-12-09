# The MIT License
# 
# Copyright (c) 2012 David Heitzman
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

require './lib/gahelloworld'
if ARGV.empty? || ARGV.include?("-h") ||  ARGV.include?("--help")
  puts "Genetic Algorithm Hello World. Ruby version by David Heitzman, 2012"
  puts "usage: ruby gahelloworld" 
  puts "  --help" 
  puts "  --size=<number>       population size"
  puts "  --crossover=<float>   portion each generation subject to replacement by new combination of two parents" 
  puts "  --mutation=<number>   chance that a newly crossed over child will mutate" 
  puts "  --elitism=<float>     portion each generation that will be preserved"
  puts "  --seed=<number>       random number seed"
end  
size = ARGV.find{|i| i.include?("--size") }
size = size.split("=")[1].to_i if size
size ||= 2048

crossover = ARGV.find{|i| i.include?("--crossover") }
crossover = crossover.split("=")[1].to_f if crossover
crossover ||= 0.8

mutation = ARGV.find{|i| i.include?("--mutation") }
mutation = mutation.split("=")[1].to_f if mutation
mutation ||= 0.3

elitism = ARGV.find{|i| i.include?("--elitism") }
elitism = elitism.split("=")[1].to_f if elitism
elitism ||= 0.1

seed = ARGV.find{|i| i.include?("--seed") }
seed = seed.split("=")[1].to_i if seed
seed ||= GAHelloWorld::RAND_SEED

puts "GAHellowWorld Ruby edition by David Heitzman"
puts "target string: #{GAHelloWorld::TARGET_GENE} " 
puts "size:#{size} crossover:#{crossover} mutation:#{mutation} elitism:#{elitism} seed:#{seed}"
max_generations = 16384
pop = GAHelloWorld::Population.new(size, crossover, elitism, mutation, seed)
  curgen = 1
  finished=false
  while curgen <= 16384 && !finished
    finished=false
    puts("Generation #{curgen}: #{pop.population[0].gene}. Fitness: #{pop.population[0].fitness}" )
    if pop.population[0].fitness == 0
      puts "Finished-- generation: #{curgen}, gene: #{pop.population.first.gene}. " 
      finished=true
    else
      pop.evolve  
    end  
    curgen += 1
    puts "Reached max generation (#{max_generations}). Current best: #{pop.population.first.gene}" if curgen > max_generations
  end 

