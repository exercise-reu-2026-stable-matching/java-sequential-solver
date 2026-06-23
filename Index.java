/** 2D index into an `n` x `n` array */
record Index(int y, int x) implements Comparable<Index> {
    @Override 
    public String toString() {
        return "(" + y + ", " + x + ")";
    }

    @Override
    public int compareTo(Index o) {
        int compare = ((Integer)this.y).compareTo(o.y);
        if (compare == 0) {
            return ((Integer)this.x).compareTo(o.x);
        }

        return compare;
    }
}
