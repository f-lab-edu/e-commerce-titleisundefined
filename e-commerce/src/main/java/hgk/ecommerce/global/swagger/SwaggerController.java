package hgk.ecommerce.global.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("")
    public String redirectSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
