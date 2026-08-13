import pandas as pd
from math import ceil, log, exp

cols = []
ns = [100, 500, 1000, 2000, 4000, 10000]
for n in ns:
    tmp = pd.read_csv(f"B_i_over_n={n}.csv", index_col=0).iloc[:, 0].to_list()
    cols.append(tmp)
max_len = max(len(c) for c in cols)
for c in cols:
    c += [0.0] * (max_len - len(c))

df: pd.DataFrame = pd.DataFrame(cols).T
df.columns = ns

print(df)
for n in ns:
    for m in ns:
        if n >= m:
            continue
        badness = df.loc[:, n] - df.loc[:, m]
        mask = (badness > 1e-15)
        print(f"bad {n}, {m}: {list(zip(df.index[mask], badness[mask]))}")
# print(df)
print(df.iloc[0, 0])
print(df.iloc[0, 1])
# DFS = [pd.read_csv(f"B_i_over_n={n}.csv") for n in NS]

