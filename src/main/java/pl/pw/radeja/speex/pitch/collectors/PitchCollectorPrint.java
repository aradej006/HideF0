package pl.pw.radeja.speex.pitch.collectors;

import java.io.PrintWriter;
import java.util.List;

public class PitchCollectorPrint {
    public static void print(List<PitchValue> pitchValues) {
        print(pitchValues, null, false);
    }

    public static PrintWriter print(List<PitchValue> pitchValues, PrintWriter printWriter, boolean logInConsole) {
        if (printWriter != null) {
            printWriter.println("PitchValues");
            printWriter.println("Frame No.\tVal");
        }
        if (logInConsole) {
            System.out.println("\n\n\n");
            System.out.println("PitchValues");
            System.out.println("Frame No.\tVal");
        }
        pitchValues.forEach(pitchValue -> pitchValue.getPitchValues().forEach(p -> {
            if (logInConsole) {
                System.out.println(pitchValue.getNumberOfFrame() + "\t" + p);
            }
            if (printWriter != null) {
                printWriter.println(pitchValue.getNumberOfFrame() + "\t" + p);
            }
        }));
        if (logInConsole) {
            System.out.println("\n\n\n");
        }
        return printWriter;
    }
}
