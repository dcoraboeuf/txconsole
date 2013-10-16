package net.txconsole.web.resource;

import lombok.Data;
import net.txconsole.core.model.RequestControlledView;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.core.model.Resource;

@Data
public class RequestControlledViewResource extends Resource<RequestControlledView> {

    private final Resource<RequestSummary> summary;

    public RequestControlledViewResource(RequestControlledView view, Resource<RequestSummary> requestSummaryResource) {
        super(view);
        this.summary = requestSummaryResource;
    }

}
