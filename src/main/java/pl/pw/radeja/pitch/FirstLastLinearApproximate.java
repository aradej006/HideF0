package pl.pw.radeja.pitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FirstLastLinearApproximate extends PitchChanger {

    public FirstLastLinearApproximate(int logLevel, Integer threshold) {
        super(logLevel, threshold);
    }

    @Override
    public List<Integer> change(List<Integer> pitches) {
        if (pitches == null) {
            throw new IllegalArgumentException("Pitches cannot be null.");
        } else if (pitches.size() < 3) {
            throw new IllegalArgumentException("Pitches size must be at least 3");
        }
        List<Integer> result = new ArrayList<>();
        Integer first = pitches.get(0);
        Integer last = pitches.get(pitches.size() - 1);
        if (shouldChange(pitches)) {
            if (logLevel >= 1) {
                System.out.println("A: " + pitches.stream().map(Objects::toString).collect(Collectors.joining(";")));
            }
            for (int i = 0; i < pitches.size(); i++) {
                result.add(LinearApprox(first, last, pitches.size(), i));
            }
            if (logLevel >= 1) {
                System.out.println("B: " + result.stream().map(Objects::toString).collect(Collectors.joining(";")));
                System.out.println("\n");
            }
            return result;
        } else {
            return pitches;
        }
    }

    private static Integer LinearApprox(Integer first, Integer last, Integer length, Integer x) {
        return ((last - first) / (length - 1)) * x + first;
    }

    public boolean shouldChange(List<Integer> pitches) {
        Integer first = pitches.get(0);
        Integer last = pitches.get(pitches.size() - 1);
        for (int i = 0; i < pitches.size(); i++) {
            Integer val = pitches.get(i);
            Integer approx = LinearApprox(first, last, pitches.size(), i);
            if (!((approx <= val && val + threshold <= approx) || (approx >= val && approx <= val + threshold))) {
                return false;
            }
        }

        return true;
    }
}
