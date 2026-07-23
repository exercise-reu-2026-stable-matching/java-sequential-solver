/** 2D index into an `n` x `n` array */
record Index(int y, int x) {
    @Override 
    public String toString() {
        return "(" + y + "," + x + ")";
    }

    public Index transpose() {
        return new Index(x, y);
    }
}