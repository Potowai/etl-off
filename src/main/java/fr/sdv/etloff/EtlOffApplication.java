package fr.sdv.etloff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EtlOffApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EtlOffApplication.class);
        // pas besoin de Tomcat pour un import en ligne de commande
        if (isEtlRun(args)) {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        app.run(args);
    }

    private static boolean isEtlRun(String[] args) {
        for (String arg : args) {
            if (arg.contains("etl.run=true")) {
                return true;
            }
        }
        return "true".equalsIgnoreCase(System.getProperty("etl.run"));
    }
}
