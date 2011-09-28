# Access the test result cell.
test = 1
answer = (ans) -> $('#actual-' + test++).text ans

# Test 1: inline ifs
number   = 42
opposite = true
answer number if opposite?

# Test 2: Square something x
square = (x) -> x * x
answer square 2

# Test 3: Array length
list = [1, 2, 3, 4, 5]
answer list.length

# Test 4: Objects
# sqrt(16) ^ 3 = 64
math =
  root:   Math.sqrt
  square: square
  cube:   (x) -> x * square x
answer math.cube(math.root(16))

# Test 5: Splats
race = (winner, runners...) ->
  answer JSON.stringify runners
race 'a', 'b', 'c'

# Test 6: Array comprehensions
# 5^3 = 125
cubes = (math.cube num for num in list)
answer cubes[4]

# Test 7: String interpolation and multiline strings
answer "Five cubed
 is #{cubes[4]}."

# Test 8: Play! tags doesn't apply here.
answer 'success'

window.checkAnswers()
