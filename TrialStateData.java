class TrialStateData {
    private final int trialIndex;
    private final Prefs prefs;
    private final boolean converges;

    public TrialStateData(int trialIndex, Prefs prefs, boolean converges) {
        this.trialIndex = trialIndex;
        this.prefs = prefs;
        this.converges = converges;
    }

    public String toCSVString(int programIndex) {
        StringBuilder builder = new StringBuilder();

        builder.append(programIndex).append(",");
        builder.append(trialIndex).append(",");

        for (int y = 0; y < prefs.n(); y++) {
            for (int x = 0; x < prefs.n(); x++) {
                int left_val = prefs.malePrefs(y, x);
                int right_val = prefs.femalePrefs(x, y);
                builder.append(left_val).append(",");
                builder.append(right_val).append(",");
            }
        }

        builder.append(converges ? 1 : 0);

        return builder.toString();
    }
}
