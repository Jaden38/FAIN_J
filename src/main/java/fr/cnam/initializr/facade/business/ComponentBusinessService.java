package fr.cnam.initializr.facade.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentBusinessService {
    private final ComponentProvider provider;

    public ComponentArchive generateComponent(ComponentRequest request) {
        validateRequest(request);
        return provider.generateComponent(request);
    }

    public List<StarterKitBusiness> getAvailableComponents() {
        return provider.getAvailableComponents();
    }

    private void validateRequest(ComponentRequest request) {
        // Business validation logic
    }
}