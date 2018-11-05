package pl.pw.radeja;

import java.util.*;

public final class PitchCollector {
    /**
     * Key - frame number
     * Value - pitch value
     */
    private static Collection<Integer[]> pitch = new ArrayList<>();
    private static Collection<Integer> pitchSingle = new ArrayList<>();


    private PitchCollector() {
    }

    public static boolean addPitch(int[] newPitch) {
        return pitch.add(Arrays.stream(newPitch).boxed().toArray(Integer[]::new));
    }

    public static boolean addPitch(int newPitch){
        return pitchSingle.add(newPitch);
    }

    public static Collection<Integer> getAllPitches() {
        Collection<Integer> values = new ArrayList<>();
        pitch.forEach(integers -> values.addAll(Arrays.asList(integers)));
        return values;
    }

    public static Collection<Integer> getAllSinglePitches(){
        return pitchSingle;
    }

    public static Collection<Integer[]> getPitch() {
        return pitch;
    }


}
