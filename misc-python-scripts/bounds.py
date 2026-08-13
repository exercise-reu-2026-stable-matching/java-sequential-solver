import pandas as pd
import numpy as np
from math import ceil, log, pow, exp

def b_stars(length: int) -> list[float]:
    out = []
    if (length <= 0): return out
    out.append(1)
    if (length == 1): return out
    out.append(exp(-1)) # 1/e
    for i in range(2, length):
        prev = out[-1]
        next_ = pow(prev, 1 / (1 - prev))
        out.append(next_)
    return out

def check_bounds(n: int) -> tuple[bool, bool]:
    path = f"B_i_over_n={n}.csv"
    df = pd.read_csv(path)
    col = df.iloc[:, 1]
    
    col = col.iloc[1:]
    
    is_ = col.index
    lower = 1 / (2 * is_ * np.log(is_))
    upper = 1 / (is_ * np.log(is_))
    lb = (col >= lower).all()
    ub = (col <= upper).all()
    
    upper_b_star = b_stars(len(is_) + 1)[1:]
    ub_star_gap_idx = (upper_b_star - col).idxmin()
    ub_star_gap = min(upper_b_star - col)
    
    return (lb, ub, ub_star_gap_idx, ub_star_gap)

for n in [100, 500, 1000, 2000, 4000, 10000]:
    print(f"{check_bounds(n)}")