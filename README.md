## Compiler From Scratch ##

Inspired by the wonderful "Compiler from Scratch" and an excuse to try out java 10.

Generates valid javascript from a tiny subset of a ruby like language. 


E.g: given:

```ruby
def addTwice(x, y)
   add(add(x, y), y)
end

def squared(x)
   multiply(x, x)
end

def addedTwiceAndSquared(x, y)
   squared(addTwice(x, y))
end
```

The following would be produced:

```javascript
function addTwice(x, y) { return add(add(x, y), y); }
function squared(x) { return multiply(x, x); }
function addTwiceAndSquared(x, y) { return squared(addTwice(x, y)); }
```
 
(where add and multiply are functions defined else where)


