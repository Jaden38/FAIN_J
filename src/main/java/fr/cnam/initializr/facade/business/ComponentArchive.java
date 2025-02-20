package fr.cnam.initializr.facade.business;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
public class ComponentArchive {
    private Resource content;

    public ComponentArchive(Resource content) {
        this.content = content;
    }
}