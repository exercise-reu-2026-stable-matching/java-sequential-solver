#!/opt/homebrew/bin/python3.10

import sys
from collections import defaultdict
import math

N_SAMPLES = 10000
# for each i, sum of |B_i| over all samples
b_sizes_total = defaultdict(lambda: 0)
a_sizes_total = defaultdict(lambda: 0)

n = None
for line in sys.stdin:
    n2, i, a_size, b_size = [int(x) for x in line.split(",")]
    assert n == None or n == n2
    n = n2
    
    b_sizes_total[i] += math.exp(-b_size*b_size/n/n)
    a_sizes_total[i] += a_size
   
print("i,E[|A_i|],E[|B_i|]^2/n^2") 
for k in sorted(b_sizes_total.keys()):
    a = a_sizes_total[k] / N_SAMPLES
    b = b_sizes_total[k] / N_SAMPLES
    ratio = b*b/a if a != 0 else float('nan')
    print(f"{k},{a:.5f},{b:.5f},{ratio:.5f}")