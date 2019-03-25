package pl.pw.radeja.weka.printers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WekaEnum {
    HIDE_F0("HideF0"),
    NO_HIDE_F0("NoHideF0");

    private final String hasHideF0;

    public static final String EXTENSION = ".arff";
}
