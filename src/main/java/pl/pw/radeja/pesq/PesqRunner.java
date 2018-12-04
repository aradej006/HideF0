package pl.pw.radeja.pesq;

import org.javatuples.Pair;
import pl.pw.radeja.pesq.common.PesqFiles;
import pl.pw.radeja.pesq.common.PesqResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class PesqRunner {
    public static List<PesqResult> run(List<PesqFiles> files) throws InterruptedException, IOException {
        List<PesqResult> results = Collections.synchronizedList(new ArrayList<>());
        Runtime rt = Runtime.getRuntime();
        Map<String, Process> processes = new HashMap<>();
        for (PesqFiles f : files) {
            String command = "D:/PracaMgr/master-thesis/PESQ +8000 " + f.getPathToReference() + " " + f.getPathToDegraded();
            Process p = rt.exec(command);
            System.out.println("Executing:\t" + command);
            processes.put(command, p);
            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        if (line.contains("P.862 Prediction (Raw MOS, MOS-LQO):")) {
                            String[] pesqResults = line.split(" ")[7].split("\t");
                            synchronized (results) {
                                results.add(new PesqResult(f, Float.parseFloat(pesqResults[0]), Float.parseFloat(pesqResults[1])));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        for (Map.Entry<String, Process> entry : processes.entrySet()) {
            entry.getValue().waitFor();
            System.out.println("Executed:\t" + entry.getKey());
        }
        return results;
    }
}
