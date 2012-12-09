
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
      pivot = rand( gene_ary.size() - 1 )
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
      @@tournamentSize = 3
      @size = size
      @seed = seed
      @crossover=crossover
      @elitism=elitism
      @mutation=mutation
      buf = []
      @size.times do |i|
        buf << Chromosome::gen_random()
      end 
      puts @seed.to_s
      srand @seed
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
# puts ARGV.inspect
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
  begin
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
  end while curgen <= 16384 && !finished

# tests
# 100.times do |i|
#   @test_chrom  = (GAHelloWorld::Chromosome.gen_random)
#   @test_chrom2  = (GAHelloWorld::Chromosome.gen_random)
#   puts  @test_chrom.gene.inspect
#   @test_chrom = @test_chrom.mutate
#   puts  @test_chrom.gene.inspect
#   kid=@test_chrom.mate(@test_chrom2)
#   puts  @test_chrom2.gene.inspect
#   puts kid.first.gene
#   puts kid.last.gene
# end

# pop.inspect

