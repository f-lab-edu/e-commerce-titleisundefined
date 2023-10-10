package hgk.ecommerce.domain.common.annotation;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
@Parameter(hidden = true)
public @interface AuthCheck {
    enum Role {
        USER, OWNER,
    }

    Role role();
}
