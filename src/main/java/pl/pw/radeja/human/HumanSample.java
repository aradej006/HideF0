package pl.pw.radeja.human;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.pw.radeja.Config;

@RequiredArgsConstructor
@Getter
public class HumanSample {
    private final String path;
    private final Float threshold;
    private final Config.HideF0Type hideF0Type;
}
