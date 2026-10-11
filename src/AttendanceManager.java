import java.util.ArrayList;

// Keeps the list of subjects and calculates overall values
public class AttendanceManager {
    private ArrayList<Subject> subjects = new ArrayList<>();

    public void add(Subject s) {
        subjects.add(s);
    }

    public void remove(int index) {
        subjects.remove(index);
    }

    public Subject get(int index) {
        return subjects.get(index);
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<Subject> list) {
        subjects = list;
    }

    // Overall attendance across all subjects
    public double getOverallPercentage() {
        int a = 0, t = 0;
        for (Subject s : subjects) {
            a += s.getAttended();
            t += s.getTotal();
        }
        if (t == 0) return 0;
        return a * 100.0 / t;
    }

    // Subject with the lowest attendance percentage
    public Subject getWeakest() {
        Subject weakest = null;
        for (Subject s : subjects) {
            if (weakest == null || s.getPercentage() < weakest.getPercentage()) {
                weakest = s;
            }
        }
        return weakest;
    }
}
