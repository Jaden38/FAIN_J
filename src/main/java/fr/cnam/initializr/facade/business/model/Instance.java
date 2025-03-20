package fr.cnam.initializr.facade.business.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
public class Instance {
    private Resource content;

    public Instance(Resource content) {
        this.content = content;
    }
}