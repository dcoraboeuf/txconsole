package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestControlledView {

    private final RequestSummary summary;
    private final TranslationDiff diff;
    private final Map<Integer, TranslationDiffControl> controls;

    public RequestControlledView(RequestView view, List<TranslationDiffControl> controls) {
        this(view.getSummary(), view.getDiff(), Maps.uniqueIndex(
                controls,
                new Function<TranslationDiffControl, Integer>() {
                    @Override
                    public Integer apply(TranslationDiffControl control) {
                        return control.getEntryId();
                    }
                }
        ));
    }

    /**
     * Exported for JSON
     */
    @SuppressWarnings("unused")
    public boolean isInvalid() {
        return controls.size() > 0;
    }
}
