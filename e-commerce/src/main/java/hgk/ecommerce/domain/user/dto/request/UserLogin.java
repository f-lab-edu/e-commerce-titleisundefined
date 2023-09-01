package hgk.ecommerce.domain.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;

@Getter
@JsonNaming(SnakeCaseStrategy.class)
@AllArgsConstructor
public class UserLogin {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
