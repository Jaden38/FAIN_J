package fr.cnam.ddst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * Classe principale de l'application Spring Boot.
 * <p>
 * Cette classe contient le point d'entrée de l'application Spring Boot et est annotée avec {@link SpringBootApplication},
 * ce qui permet de configurer et de lancer automatiquement l'application.
 * <p>
 * La méthode {@code main} utilise la méthode {@link SpringApplication#run(Class, String...)} pour démarrer l'application.
 */

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "fr.cnam.ddst.controller.rest",
                "fr.cnam.ddst.service",
                "fr.cnam.ddst.config",
                "org.openapitools.configuration"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        lazyInit = true
)
public class Application {

    /**
     * Point d'entrée principal de l'application Spring Boot.
     * <p>
     * Cette méthode démarre l'application en appelant {@link SpringApplication#run(Class, String...)} avec la classe {@code Application}.
     *
     * @param args les arguments de ligne de commande passés à l'application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}