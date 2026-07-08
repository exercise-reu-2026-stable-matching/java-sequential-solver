#!/opt/homebrew/bin/python3.10

import sys
from collections import defaultdict

# for each i
sizes = defaultdict(lambda: (0, 0))

n = None
for line in sys.stdin:
    n2, i, size = [int(x) for x in line.split(",")]
    assert n == None or n == n2
    n = n2
    
    total, count = sizes[i]
    sizes[i] = (total + size, count + 1)
   
print("i,|B_i|/n") 
for k in sorted(sizes.keys()):
    print(f"{k},{sizes[k][0] / sizes[k][1] / n:.10f}", flush=True)