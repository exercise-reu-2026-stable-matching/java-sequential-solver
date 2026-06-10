class Main {
    public static void main(String[] args) {
        Pair<Prefs, Permutation> example = Examples.matthewExample;
        PII pii = new PII(example.fst());
        Permutation mensMatches = example.snd();
        
        var result = pii.runOne(5, mensMatches);
        System.out.println("Success: " + result.snd());
        System.out.println("Result: " + result.fst());
    }
}
