package fr.cnam.initializr.facade.business.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Project {
    private byte[] content;
    private String fileName;
    private String contentType;
}