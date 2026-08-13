#!/opt/homebrew/bin/python3.10

import sys
from collections import defaultdict

N_SAMPLES = 100000
# for each i, sum of |B_i| over all samples
b_sizes_total = defaultdict(lambda: 0)

n = None
for line in sys.stdin:
    n2, i, b_size = [int(x) for x in line.split(",")]
    assert n == None or n == n2
    n = n2
    
    b_sizes_total[i] += min(b_size, 1)
   
print("i,P[|B_i| >= 1]") 
for k in sorted(b_sizes_total.keys()):
    print(f"{k},{b_sizes_total[k] / N_SAMPLES:.60f}")