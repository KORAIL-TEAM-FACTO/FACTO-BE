package team.java.facto_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("team.java.facto_be")
@SpringBootApplication
public class FactoBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactoBeApplication.class, args);
    }

}
