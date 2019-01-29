package pl.pw.radeja;


import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.Bits;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public final class BitsCollector {
    private String path;
    private Integer threshold;
    private List<SpeexBits> bitsToSave = new ArrayList<>();

    public boolean addBits(NamesOfBits name, int data, int nbBits) {
        return bitsToSave.add(new SpeexBits(name, data, nbBits));
    }

    public boolean addSize(int size) {
        return bitsToSave.add(new SpeexBits(NamesOfBits.SIZE, size, getBitsToSaveByName(NamesOfBits.SIZE).size() + 1));
    }

    public void save(@NotNull Bits bits) {
        bitsToSave.forEach(pair -> bits.pack(pair.getBitsData(), pair.getNumberOfBits()));
    }

    public List<SpeexBits> getBitsToSave() {
        return bitsToSave;
    }

    public List<SpeexBits> getBitsToSaveByName(NamesOfBits name) {
        return bitsToSave.stream().filter(o -> o.getNamesOfBits().equals(name)).collect(Collectors.toList());
    }

    public String getSampleName() {
        String temp = path.split("TIMIT_")[1];
        return temp.charAt(0) + temp.split("-")[0].substring(2);
    }
}
