package cafe.service.infra;

import cafe.service.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class CafeOrderHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<CafeOrder>> {

    @Override
    public EntityModel<CafeOrder> process(EntityModel<CafeOrder> model) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/makestart")
                .withRel("makestart")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/makecomplete")
                .withRel("makecomplete")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/orderapprove")
                .withRel("orderapprove")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/ordercancel")
                .withRel("ordercancel")
        );

        return model;
    }
}
