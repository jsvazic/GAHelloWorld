require 'test/unit'
require File.expand_path("../../lib/gahelloworld",__FILE__ )

class GAHelloWorldTest < Test::Unit::TestCase

  def test_should_accept_command_line_inputs   
    assert_equal 1,0     
  end

  def test_should_accept_command_line_inputs   
    assert_equal 1,0     
  end

end

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

