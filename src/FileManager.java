import java.io.*;
import java.util.ArrayList;

// Saves and loads the subjects in a text file (attendance.txt)
public class FileManager {
    static final String FILE = "attendance.txt";

    // One line per subject: type,name,attended,total,remaining
    static void save(ArrayList<Subject> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Subject s : list) {
                pw.println(s.getType() + "," + s.getName() + "," + s.getAttended()
                        + "," + s.getTotal() + "," + s.getRemaining());
            }
        } catch (IOException e) {
            System.out.println("Could not save data");
        }
    }

    // Returns an empty list if the file is missing; bad lines are skipped
    static ArrayList<Subject> load() {
        ArrayList<Subject> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] p = line.split(",");
                    String name = p[1];
                    int a = Integer.parseInt(p[2].trim());
                    int t = Integer.parseInt(p[3].trim());
                    int r = Integer.parseInt(p[4].trim());
                    if (p[0].equals("Lab")) list.add(new LabSubject(name, a, t, r));
                    else list.add(new Subject(name, a, t, r));
                } catch (Exception e) {
                    // skip a corrupted line
                }
            }
        } catch (IOException e) {
            // no file yet: start empty
        }
        return list;
    }
}
