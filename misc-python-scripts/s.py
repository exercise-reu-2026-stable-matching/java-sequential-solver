import pandas as pd

def get_s(n: int) -> int:
    path = f"B_i_over_n={n}.csv"
    df = pd.read_csv(path)
    col = df.iloc[:, 1]
    s = sum(col * n >= 1)
    assert all(col[:s] * n >= 1)
    assert all(col[s:] * n < 1)
    return s

for n in [100, 500, 1000, 2000, 4000, 10000]:
    print(f"{n},{get_s(n)}")