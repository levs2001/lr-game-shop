package lr;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "Game shop Api",
    description = "lr company", version = "1.0.0",
    contact = @Contact(
        name = "Lev Saskov and Ruslan Yanbekov"
    )
))
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
