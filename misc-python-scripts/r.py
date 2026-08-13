import pandas as pd
from math import log, ceil

def get_r(n: int) -> int:
    path = f"B_i_over_n={n}.csv"
    df = pd.read_csv(path, index_col="i")
    i0 = ceil(2 * n / log(n))
    col = df.loc[(i0 + 1):, "|B_i|/n"] # this is 1-indexed!
    # return the min r that makes col fit under C*r^(i-1) where C is the initial value of the sequence after i0
    C = col.iloc[0]
    idx_offset = col.index - i0 - 1
    mask = (idx_offset != 0) & (col != 0.0)
    r_stars = (col[mask] / C) ** (1.0 / idx_offset[mask])
    maxidx = r_stars.idxmax()
    r_star = r_stars.max()
    r_i0 = col.iloc[1] / C
    return (maxidx, r_i0, r_star)

for n in [100, 500, 1000, 2000, 4000, 10000]:
    (maxidx, r_i0, r_star) = get_r(n)
    print(f"{n},{maxidx},{r_i0},{r_star}")
    # print(f"{n},{r},{1.0 - r}")