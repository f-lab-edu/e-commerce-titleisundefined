package hgk.ecommerce.global.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        OpenAPI openApi = new OpenAPI()
                .components(new Components())
                .info(apiInfo());
        return openApi;
    }


    private Info apiInfo() {
        return new Info()
                .title("E-COMMERCE")
                .description("E-COMMERCE-SWAGGER")
                .version("1.0.0");
    }
}
