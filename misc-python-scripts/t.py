import pandas as pd
from math import ceil, log

def get_t(n: int) -> int:
    path = f"B_i_over_n={n}.csv"
    df = pd.read_csv(path)
    col = df.iloc[:, 1]
    i0 = ceil(2 * n / log(n))
    t = n * sum(col[i0:]) # 1-indexed it's sum from i_0 + 1 to the end
    return t

for n in [100, 500, 1000, 2000, 4000, 10000]:
    print(f"{get_t(n)}")