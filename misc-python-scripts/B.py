import pandas as pd
from math import ceil, log, exp

# B(n, i_0)
def get_B(n: int) -> int:
    path = f"B_i_over_n={n}.csv"
    df = pd.read_csv(path)
    col = df.iloc[:, 1]
    i0 = ceil(2 * n / log(n))
    B = col[i0] * n
    return B

for n in [100, 500, 1000, 2000, 4000, 10000]:
    print(f"{n},{get_B(n)}")