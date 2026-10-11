# Attendance Predictor - How Many Classes Can I Miss?

A Java Swing application made as the Java Mini Project for B.Sc. Multimedia and
Web Technology, Nilgiri College of Arts and Science (Autonomous).

## Description
Students often learn their attendance percentage only when it is already low.
This application stores subject-wise attendance and predicts how many classes can
still be missed, how many must be attended to reach the required percentage, and
what happens if the next few classes are skipped.

## Features
- Add and delete subjects (Theory and Lab, with different required percentages), and mark classes present or absent
- Current attendance percentage per subject and overall
- Safe absences right now and classes needed from the remaining classes
- "What if I skip the next N classes?" simulator
- Colour-coded status: green (safe), yellow (warning), red (danger)
- Data saved automatically in `attendance.txt`

## Java Concepts Used
Classes and objects, encapsulation, inheritance (`LabSubject extends Subject`),
polymorphism (overridden `getRequiredPercent()`), `ArrayList`, exception handling,
file handling, Swing GUI.

## How to Run
1. Install JDK 17 or newer.
2. Clone the repository: `git clone https://github.com/ihsanmhd344-hash/AttendancePredictor.git`
3. Open the `src` folder in a terminal and run:
   ```
   javac *.java
   java Main
   ```
   (or open the folder in IntelliJ IDEA and run `Main.java`)

## Screenshots
See the `screenshots` folder.

## Note
All data used for testing is sample data.
