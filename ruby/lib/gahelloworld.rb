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

module GAHelloWorld
  RAND_SEED=srand
  TARGET_GENE='Hello World!'
  ALLOWED_LETTERS = (32..122).to_a.map{|i| i.chr}

  class Chromosome
    attr_reader :gene_ary, :target_ary, :gene

    class << self
      def gen_random()
        str=''
        TARGET_GENE.size.times do |i|
          str << ALLOWED_LETTERS[rand(ALLOWED_LETTERS.size)-1]
        end
        Chromosome.new(str)
      end 

      def to_int_array(str)
        #convenience method to get an array of strings for any string, compatible with ruby 1.9.3 and 1.8.7
        out=[]
          str.each_byte do |c| out << c end 
        out 
      end
    end

    def initialize(str='')
      @gene=str == '' ? Chromosome.gen_random.gene : str
      @gene_ary ||= Chromosome.to_int_array(@gene)
      @target_ary ||= Chromosome.to_int_array(TARGET_GENE)
    end 

    def fitness
      @fitness ||= 
        begin
          #normal -- matches the target string
          diff=0
          gene_ary.size.times do |i| diff += (gene_ary[i].to_i - target_ary[i].to_i).abs  end
          diff
        end
    end 

    def mate partner
      #split the chromosome at some random point. 
      #create two new chromosomes and return them. 
      # chrom1 gets the first half from itself and the second from the partner
      # chrom2 gets the first half from the partner and the second from itself
      pivot = rand( gene_ary.size() - 1) 
      ng1= gene[0..pivot] + partner.gene[pivot+1..-1]
      ng2= partner.gene[0..pivot] + gene[pivot+1..-1]
      [ Chromosome.new(ng1) , Chromosome.new(ng2) ]
    end 

    def mutate
      newstr=@gene.clone
      newstr[rand(@gene.size)] = ALLOWED_LETTERS[ rand(ALLOWED_LETTERS.size) ]
      Chromosome.new newstr
    end     
    

  end

  class Population
    attr_accessor :population
    Tourney_size = 3

    def each(&block)
      @population.each do |i| 
        block.call(i)
      end
    end 

    def initialize(size, crossover, elitism, mutation, seed)
      @size = size
      @seed = seed
      srand @seed
      @crossover=crossover
      @elitism=elitism
      @mutation=mutation
      buf = []
      @size.times do |i|
        buf << Chromosome::gen_random()
      end 
      # puts @seed.to_s
      @population = buf.sort!{ |a,b| a.fitness <=> b.fitness }
    end 

    def tournament_selection
      best = @population[rand(@population.size)]
      Tourney_size.times do |i|
        cont = @population[rand(@population.size)]
        best = cont if cont.fitness < best.fitness
      end 
      best
    end 

    def evolve
      # inspect
      elitism_mark=(@elitism*@population.size).to_i - 1
      buf = @population[0..elitism_mark]
      sub_pop=@population[elitism_mark+1..-1]
      sub_pop.each_with_index do |chrom, ind|
        if rand <= @crossover
          parent1=tournament_selection
          parent2=tournament_selection
          children = parent1.mate parent2
          children[0] = children[0].mutate if rand < @mutation
          children[1] = children[1].mutate if rand < @mutation
          buf += children
        else
          chrom = chrom.mutate if rand < @mutation
          buf << chrom
        end       
        break if buf.size >= @size
      end  
      @population = (buf+@population[elitism_mark+1...@size]).sort!{|a,b| a.fitness <=> b.fitness}     
      # inspect
    end

    def inspect
      ind ||= -1 
      @population[0,5].each do |chrome|
        ind += 1  
        puts "[" + ind.to_s + "] "+chrome.gene + ": fitness => " + chrome.fitness.to_s
      end
    end
  end 

end 
