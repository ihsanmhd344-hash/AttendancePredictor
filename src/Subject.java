// Stores the attendance of one subject and does all the prediction calculations
public class Subject {
    private String name;
    private int attended;    // classes attended so far
    private int total;       // classes held so far
    private int remaining;   // classes still left in the semester

    public Subject(String name, int attended, int total, int remaining) {
        this.name = name;
        this.attended = attended;
        this.total = total;
        this.remaining = remaining;
    }

    // Required attendance percentage (LabSubject overrides this)
    public double getRequiredPercent() {
        return 75.0;
    }

    public String getType() {
        return "Theory";
    }

    // Marks one class as attended
    public void markPresent() {
        attended++;
        total++;
        if (remaining > 0) remaining--;
    }

    // Marks one class as missed
    public void markAbsent() {
        total++;
        if (remaining > 0) remaining--;
    }

    // Current attendance percentage
    public double getPercentage() {
        if (total == 0) return 0;
        return attended * 100.0 / total;
    }

    // How many classes can be missed right now and still stay at the required %
    public int safeAbsencesNow() {
        double r = getRequiredPercent() / 100;
        int x = (int) Math.floor(attended / r - total + 0.000001);
        return Math.max(x, 0);
    }

    // How many of the remaining classes must be attended to finish at the required %
    public int mustAttendOfRemaining() {
        double r = getRequiredPercent() / 100;
        int x = (int) Math.ceil(r * (total + remaining) - attended - 0.000001);
        return Math.max(x, 0);
    }

    // How many of the remaining classes can still be missed (-1 = target not possible)
    public int canStillMiss() {
        int must = mustAttendOfRemaining();
        if (must > remaining) return -1;
        return remaining - must;
    }

    // Classes to attend in a row to get back to the required %
    public int classesInARowNeeded() {
        if (getPercentage() >= getRequiredPercent()) return 0;
        double r = getRequiredPercent() / 100;
        return (int) Math.ceil((r * total - attended) / (1 - r) - 0.000001);
    }

    // Attendance % if the student skips the next n classes
    public double whatIfSkip(int n) {
        return attended * 100.0 / (total + n);
    }

    // SAFE (green), WARNING (yellow), DANGER (red)
    public String getStatus() {
        double p = getPercentage();
        double req = getRequiredPercent();
        if (p >= req + 5) return "SAFE";
        if (p >= req) return "WARNING";
        return "DANGER";
    }

    public String getName() { return name; }
    public int getAttended() { return attended; }
    public int getTotal() { return total; }
    public int getRemaining() { return remaining; }
}
