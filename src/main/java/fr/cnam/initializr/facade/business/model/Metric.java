package fr.cnam.initializr.facade.business.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
public class Metric {
    private String dds;
    private String codeModule;
    private LocalDate dateInstanciation;
    private String typeModule;
    private String typeSK;
    private String versionSK;
    private String usecases;
}