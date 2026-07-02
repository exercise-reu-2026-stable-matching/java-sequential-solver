#!/opt/homebrew/bin/python3.10

import sys

num = 0.0
den = 0

for line in sys.stdin:
    num += float(line)
    den += 1
    
print(num / den if den != 0 else "NaN")