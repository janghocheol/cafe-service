package cafe.service.infra;
import cafe.service.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class MenuHateoasProcessor implements RepresentationModelProcessor<EntityModel<Menu>>  {

    @Override
    public EntityModel<Menu> process(EntityModel<Menu> model) {

        
        return model;
    }
    
}
