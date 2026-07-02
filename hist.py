#!/opt/homebrew/bin/python3.10

import sys
import matplotlib.pyplot as plt

data = []
for line in sys.stdin:
    data.append(float(line))
    
print(f"Mean: {sum(data)/len(data):.3f}")
plt.hist(data, bins=25)
plt.show()