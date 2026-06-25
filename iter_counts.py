#!/usr/bin/env python3

# vibe-coded!

"""Live histogram of iteration counts read from stdin.

Reads non-negative integers (one per line) from stdin and shows a matplotlib
histogram with one bin per integer, updating in real time as more data arrives.

Usage:
    java Main <nSize> ... | python3 iter_counts.py [nSize]

If nSize is given, the x-axis extends to nSize**2; otherwise it auto-fits the
data.
"""

import sys
import shutil
import threading
import argparse
from collections import Counter

import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation


def read_stdin(data, lock):
    """Read integers from stdin in a background thread."""
    for line in sys.stdin:
        line = line.strip()
        if not line:
            continue
        try:
            value = int(line)
        except ValueError:
            continue
        if value >= 0:
            with lock:
                data[value] += 1


# Stats shown, in display order.
_SUMMARY_LABELS = ["Min.", "Median", "Mean", "Max.", "SD"]


def _quantile_type7(vals, cum, n, p):
    """Weighted type-7 quantile (R's default) from sorted distinct values.

    vals: sorted distinct values; cum: cumulative counts (count of samples
    <= vals[j]); n: total sample count.
    """
    h = (n - 1) * p
    lo = int(np.floor(h))
    frac = h - lo
    v_lo = float(vals[np.searchsorted(cum, lo, side="right")])
    if frac == 0:
        return v_lo
    v_hi = float(vals[np.searchsorted(cum, lo + 1, side="right")])
    return v_lo + frac * (v_hi - v_lo)


def summary_stats(counts):
    """Summary stats of integer samples held as {value: count}."""
    items = sorted(counts.items())
    vals = np.array([k for k, _ in items], dtype=np.float64)
    cnts = np.array([c for _, c in items], dtype=np.int64)
    n = int(cnts.sum())
    cum = np.cumsum(cnts)
    mean = float((vals * cnts).sum() / n)
    # Sample standard deviation (ddof=1); undefined for n < 2.
    sd = float(np.sqrt((cnts * (vals - mean) ** 2).sum() / (n - 1))) \
        if n > 1 else float("nan")
    return {
        "Min.": float(vals[0]),
        "Median": _quantile_type7(vals, cum, n, 0.5),
        "Mean": mean,
        "SD": sd,
        "Max.": float(vals[-1]),
    }


def format_summary(counts):
    """Two aligned lines (header + values)."""
    header = "".join(f"{lab:>10}" for lab in _SUMMARY_LABELS)
    if not counts:
        row = "".join(f"{'-':>10}" for _ in _SUMMARY_LABELS)
    else:
        stats = summary_stats(counts)
        row = "".join(f"{stats[lab]:>10.4g}" for lab in _SUMMARY_LABELS)
    return [header, row]


def format_counts(counts):
    """One line of value:count pairs, truncated to the terminal width."""
    if not counts:
        return "counts: -"
    pairs = "  ".join(f"{v}:{c}" for v, c in sorted(counts.items()))
    line = "counts: " + pairs
    width = shutil.get_terminal_size((80, 24)).columns
    if len(line) > width:
        line = line[:width - 1] + "…"
    return line


def print_summary(counts, state):
    """Print the summary block, overwriting the previous one via ANSI."""
    lines = format_summary(counts) + [format_counts(counts)]
    out = sys.stdout
    if state["printed"]:
        out.write(f"\033[{len(lines)}A")  # cursor up over the old block
    for line in lines:
        out.write("\033[2K" + line + "\n")  # clear line, then rewrite
    out.flush()
    state["printed"] = True


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("nSize", nargs="?", type=int, default=None,
                        help="preference matrix size; caps x-axis at nSize**2")
    args = parser.parse_args()

    data = Counter()
    lock = threading.Lock()
    nsize_str = "?" if args.nSize is None else str(args.nSize)

    reader = threading.Thread(target=read_stdin, args=(data, lock), daemon=True)
    reader.start()

    fig, ax = plt.subplots()
    summary_state = {"printed": False}

    def update(_frame):
        with lock:
            counts = dict(data)

        total = sum(counts.values())
        print_summary(counts, summary_state)

        ax.clear()
        ax.set_xlabel("iterations")
        ax.set_ylabel("count")
        ax.set_title(
            f"Iteration counts (nSize = {nsize_str}, # samples = {total:,})")

        top = max(counts) if counts else 0

        # One bin per non-negative integer in [0, top].
        heights = np.zeros(top + 1, dtype=np.int64)
        for value, count in counts.items():
            if value <= top:
                heights[value] = count
        ax.bar(range(len(heights)), heights, width=1.0, align="center",
               edgecolor="black")
        ax.set_xlim(-0.5, top + 0.5)

    # Keep a reference so the animation isn't garbage-collected.
    _anim = FuncAnimation(fig, update, interval=300, cache_frame_data=False)
    plt.show()


if __name__ == "__main__":
    main()
