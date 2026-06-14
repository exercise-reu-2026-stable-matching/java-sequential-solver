class Examples {
    // Initial matching in the example from the slides
    public static final Pair<Prefs, Permutation> slidesExample;
    static {
        int[][] malePrefs = {
            { 4, 2, 3, 1 },
            { 3, 1, 2, 4 },
            { 2, 4, 1, 3 },
            { 1, 4, 3, 2 }
        };
        int[][] femalePrefs = {
            { 1, 4, 2, 3 },
            { 1, 2, 3, 4 },
            { 4, 2, 3, 1 },
            { 3, 1, 4, 2 }
        };
        slidesExample = new Pair<>(
            new Prefs(malePrefs, femalePrefs),
            new Permutation(new int[]{ 0, 3, 2, 1 }));
    }

    // Jeffrey's example that we worked out together
    public static final Pair<Prefs, Permutation> jeffreyExample;
    static {
        int[][] malePrefs = {
            { 3, 4, 5, 1, 6, 2 },
            { 1, 5, 2, 6, 4, 3 },
            { 6, 1, 4, 5, 2, 3 },
            { 5, 6, 1, 3, 2, 4 },
            { 2, 4, 5, 1, 6, 3 },
            { 1, 3, 2, 5, 4, 6 }
        };
        int[][] femalePrefs = {
            { 2, 4, 5, 1, 6, 3 },
            { 2, 5, 3, 6, 1, 4 },
            { 1, 2, 6, 5, 4, 3 },
            { 2, 4, 1, 6, 5, 3 },
            { 6, 5, 4, 2, 3, 1 },
            { 3, 5, 1, 6, 2, 4 }
        };
        jeffreyExample = new Pair<>(
            new Prefs(malePrefs, femalePrefs),
            Permutation.identity(6));
    }

    // Matthew's example
    public static final Pair<Prefs, Permutation> matthewExample;
    static {
        int[][] malePrefs = {
            {  2,  4, 10,  9,  5,  3,  8,  6,  1,  7 },
            {  2, 10,  5,  8,  7,  4,  9,  1,  6,  3 },
            {  1,  2,  9,  6,  8,  5, 10,  7,  3,  4 },
            {  2,  4,  5,  8,  9, 10,  3,  6,  7,  1 },
            {  6, 10,  7,  4,  3,  1,  9,  8,  2,  5 },
            {  9,  2, 10,  6,  4,  1,  7,  5,  8,  3 },
            {  6,  5,  8,  3,  9,  7,  1, 10,  2,  4 },
            {  4,  7,  8,  9,  3, 10,  5,  2,  6,  1 },
            {  9,  3,  4,  1,  6,  7, 10,  8,  5,  2 },
            {  9, 10,  1,  5,  6,  3,  2,  8,  4,  7 }
        };
        int[][] femalePrefs = {
            {  5, 10,  2,  6,  3,  1,  8,  4,  9,  7 },
            {  1, 10,  9,  2,  7,  4,  3,  5,  8,  6 },
            {  5,  8,  7,  3,  4,  9,  2,  6, 10,  1 },
            {  6,  1,  8,  4, 10,  3,  7,  9,  2,  5 },
            {  4,  3,  2,  1,  5,  6,  7,  9, 10,  8 },
            {  8,  2,  9,  5,  1,  4,  6,  3,  7, 10 },
            {  4,  2,  7,  1,  8,  5, 10,  6,  3,  9 },
            {  9,  6,  3, 10,  8,  2,  4,  5,  7,  1 },
            {  3,  2,  6,  5,  4,  1,  8,  7,  9, 10 },
            {  8,  1,  4,  3,  9,  2,  7, 10,  6,  5 }
        };
        matthewExample = new Pair<>(
            new Prefs(malePrefs, femalePrefs),
            new Permutation(new int[]{ 1, 7, 3, 0, 5, 6, 9, 8, 4, 2 }));
    }

    public static final Pair<Prefs, Permutation> threeExample;
    static {
        int[][] malePrefs = {
            { 1, 2, 3 },
            { 1, 2, 3 },
            { 1, 2, 3 }
        };
        int[][] femalePrefs = {
            { 3, 2, 1 },
            { 2, 3, 1 },
            { 3, 1, 2 }
        };
        threeExample = new Pair<>(
            new Prefs(malePrefs, femalePrefs),
            Permutation.identity(3));
    }

    public static final Pair<Prefs, Permutation> threeExample2;
    static {
        int[][] malePrefs = {
            { 3, 2, 1 },
            { 3, 2, 1 },
            { 2, 1, 3 }
        };
        int[][] femalePrefs = {
            { 3, 2, 1 },
            { 3, 1, 2 },
            { 3, 2, 1 }
        };
        threeExample2 = new Pair<>(
            new Prefs(malePrefs, femalePrefs),
            Permutation.identity(3));
    }
}