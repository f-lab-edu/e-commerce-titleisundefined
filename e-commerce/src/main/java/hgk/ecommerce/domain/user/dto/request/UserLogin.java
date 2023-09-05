package hgk.ecommerce.domain.user.dto.request;

import com.fasterxml.jackson.databind.EnumNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {
    @NotBlank(message = "로그인 아이디는 공백일 수 없습니다.")
    private String loginId;
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;
}
