package fr.cnam.initializr.facade.provider.mapper;

import fr.cnam.initializr.facade.business.model.Metric;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ModuleResource;
import org.springframework.stereotype.Component;

@Component
public class MetricMapper {

    public ModuleResource toModuleResource(Metric moduleMetric) {
        ModuleResource moduleResource = new ModuleResource();
        moduleResource.setDds(moduleMetric.getDds());
        moduleResource.setCodeModule(moduleMetric.getCodeModule());
        moduleResource.setDateInstanciation(moduleMetric.getDateInstanciation());
        moduleResource.setTypeModule(moduleMetric.getTypeModule());
        moduleResource.setTypeSK(moduleMetric.getTypeSK());
        moduleResource.setVersionSK(moduleMetric.getVersionSK());
        moduleResource.setUsecases(moduleMetric.getUsecases());
        return moduleResource;
    }
}