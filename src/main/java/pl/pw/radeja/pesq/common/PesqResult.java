package pl.pw.radeja.pesq.common;

import pl.pw.radeja.Config;

public class PesqResult {
    private PesqFiles pesqFiles;
    private Float rawMos;
    private Float mosLqo;

    public PesqResult(PesqFiles pesqFiles, Float rawMos, Float mosLqo) {
        this.pesqFiles = pesqFiles;
        this.rawMos = rawMos;
        this.mosLqo = mosLqo;
    }

    public PesqFiles getPesqFiles() {
        return pesqFiles;
    }

    public Float getRawMos() {
        return rawMos;
    }

    public Float getMosLqo() {
        return mosLqo;
    }

    public String getSampleName() {
        return Config.getSampleNameFromPath(pesqFiles.getPathToDegraded());
    }

    public Integer getSampleThreshold() {
        return Integer.parseInt(pesqFiles.getPathToDegraded().split("-")[3].split("_")[0]);
    }
}
