// A lab subject: same as Subject but with a higher required percentage
// Inheritance: extends Subject.  Polymorphism: overrides getRequiredPercent()
public class LabSubject extends Subject {

    public LabSubject(String name, int attended, int total, int remaining) {
        super(name, attended, total, remaining);
    }

    @Override
    public double getRequiredPercent() {
        return 80.0;   // change this value to match your college's lab rule
    }

    @Override
    public String getType() {
        return "Lab";
    }
}
