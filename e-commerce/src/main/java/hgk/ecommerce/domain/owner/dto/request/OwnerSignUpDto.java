package hgk.ecommerce.domain.owner.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OwnerSignUpDto {
    @NotBlank(message = "로그인 아이디는 공백일 수 없습니다.")
    private String loginId;
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;
}
