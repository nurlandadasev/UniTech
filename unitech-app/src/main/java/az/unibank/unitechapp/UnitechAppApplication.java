package az.unibank.unitechapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "az.unibank")
@ComponentScan(basePackages = "az.unibank")
@EnableJpaRepositories(basePackages = "az.unibank.persistence")
@SpringBootApplication
public class UnitechAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnitechAppApplication.class, args);
    }


}
