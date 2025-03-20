package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.Instance;
import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.business.model.StarterKit;
import fr.cnam.initializr.facade.business.port.TonicProvider;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class ComponentService {
    private final MetricService metricService;
    private final TonicProvider tonicProvider;
    public static final List<StarterKit> AVAILABLE_COMPONENT_STARTER_KITS = List.of(StarterKit.TONIC);

    public Instance generateComponent(@Valid Component component) {
        validateComponentStarterKit(component.getStarterKit());
        validateFeatures(component);

        Instance instance = null;
        if (component.getStarterKit() == StarterKit.TONIC) {
            instance = tonicProvider.generateComponent(component);
        }

        metricService.recordComponentGeneration(component);

        return instance;
    }

    public List<StarterKit> getAvailableStarterKits() {
        return AVAILABLE_COMPONENT_STARTER_KITS;
    }

    public List<String> getComponentFeatures(StarterKit starterKit) {
        validateComponentStarterKit(starterKit);

        List<String> features = Collections.emptyList();
        if (starterKit == StarterKit.TONIC) {
            features = tonicProvider.getComponentFeatures();
        }

        return features;
    }

    public void validateComponentStarterKit(StarterKit starterKit) {
        if (AVAILABLE_COMPONENT_STARTER_KITS.stream().noneMatch(availableStarterKit -> availableStarterKit.equals(starterKit))) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid starter kit : " + starterKit + ". Available starter kits are: " + AVAILABLE_COMPONENT_STARTER_KITS
            );
        }
    }

    private void validateFeatures(Component component) {
        List<String> requestedFeatures = component.getFeatures();
        if (requestedFeatures == null || requestedFeatures.isEmpty()) {
            log.info("No features requested for type: {}, skipping validation", component.getStarterKit());
            return;
        }

        List<String> availableFeatures = getComponentFeatures(component.getStarterKit());
        if (availableFeatures.isEmpty()) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "No features available for starter kit type: " + component.getStarterKit()
            );
        }

        List<String> invalidFeatures = requestedFeatures.stream()
                .filter(feature -> !availableFeatures.contains(feature))
                .toList();

        if (!invalidFeatures.isEmpty()) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    String.format("%s. Available features are: %s",
                            invalidFeatures, availableFeatures)
            );
        }

        log.info("Successfully validated {} features for type {}", requestedFeatures.size(), component.getStarterKit());
        log.debug("Validated features: {}", requestedFeatures);
    }
}