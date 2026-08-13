#!/opt/homebrew/bin/python3.10

import sys
from collections import defaultdict

N_SAMPLES = 1000000
# for each i, sum of |B_i| over all samples
b_sizes_total = defaultdict(lambda: 0)

n = None
for line in sys.stdin:
    n2, i, b_size = [int(x) for x in line.split(",")]
    assert n == None or n == n2
    n = n2
    
    b_sizes_total[i] += b_size
   
print("i,|B_i|/n") 
for k in sorted(b_sizes_total.keys()):
    print(f"{k},{b_sizes_total[k] / N_SAMPLES / n:.60f}")