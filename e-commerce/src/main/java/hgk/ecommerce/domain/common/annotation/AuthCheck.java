package hgk.ecommerce.domain.common.annotation;

import hgk.ecommerce.global.utils.SessionUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static hgk.ecommerce.global.utils.SessionUtils.*;
import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
public @interface AuthCheck {

}
