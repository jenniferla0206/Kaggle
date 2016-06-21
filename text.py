def foo3(x,y,z):
 if x > y:
  tmp = y
  y = x
  x = tmp
 if y > z:
  tmp = z
  z = y
  y = tmp
 if x > y:
  tmp = y
  y = x
  x = tmp
 return [x,y,z]