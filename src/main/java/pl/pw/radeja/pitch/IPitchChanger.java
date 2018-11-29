package pl.pw.radeja.pitch;

import java.util.List;

public interface IPitchChanger {

    List<Integer> change(List<Integer> pitches);

    boolean shouldChange(List<Integer> pitches);
}
