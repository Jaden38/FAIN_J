package fr.cnam.initializr.facade.business.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ModuleMetric {
    private String dds;
    private String codeModule;
    private LocalDate dateInstanciation;
    private String typeModule;
    private String typeSK;
    private String versionSK;
    private String usecases;
}