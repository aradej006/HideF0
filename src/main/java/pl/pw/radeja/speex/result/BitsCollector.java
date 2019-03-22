package pl.pw.radeja.speex.result;


import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.Bits;
import pl.pw.radeja.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public final class BitsCollector {
    private String path;
    private Integer threshold;
    private List<SpeexBits> bitsToSave = new ArrayList<>();

    public boolean addBits(SpeexBitsName name, int data, int nbBits) {
        return bitsToSave.add(new SpeexBits(name, data, nbBits));
    }

    public boolean addSize(int size) {
        return bitsToSave.add(new SpeexBits(SpeexBitsName.SIZE, size, getBitsToSaveByName(SpeexBitsName.SIZE).size() + 1));
    }

    public void save(@NotNull Bits bits) {
        bitsToSave.forEach(pair -> bits.pack(pair.getBitsData(), pair.getNumberOfBits()));
    }

    public List<SpeexBits> getBitsToSave() {
        return bitsToSave;
    }

    public List<SpeexBits> getBitsToSaveByName(SpeexBitsName name) {
        return bitsToSave.stream().filter(o -> o.getSpeexBitsName().equals(name)).collect(Collectors.toList());
    }

    public String getSampleName() {
        return Config.getSampleNameFromPath(path);
    }
}
