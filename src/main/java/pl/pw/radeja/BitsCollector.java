package pl.pw.radeja;


import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.Bits;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BitsCollector {
    private List<Triplet<NamesOfBits, Integer, Integer>> bitsToSave = new ArrayList<>();

    public boolean addBits(NamesOfBits name, int data, int nbBits) {
        return bitsToSave.add(new Triplet<>(name, data, nbBits));
    }

    public boolean addSize(int size) {
        return bitsToSave.add(new Triplet<>(NamesOfBits.SIZE, size, getBitsToSaveByName(NamesOfBits.SIZE).size() + 1));
    }

    public void save(@NotNull Bits bits) {
        bitsToSave.forEach(pair -> bits.pack(pair.getValue1(), pair.getValue2()));
    }

    public List<Triplet<NamesOfBits, Integer, Integer>> getBitsToSave() {
        return bitsToSave;
    }

    public List<Triplet<NamesOfBits, Integer, Integer>> getBitsToSaveByName(NamesOfBits name) {
        return bitsToSave.stream().filter(o -> o.getValue0().equals(name)).collect(Collectors.toList());
    }
}
