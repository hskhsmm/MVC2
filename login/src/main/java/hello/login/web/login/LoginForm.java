package hello.login.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
public class LoginForm {

    @NotEmpty(message = "로그인 ID는 필수입니다.")
    private String loginId; // 사용자 아이디

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password; // 사용자 비밀번호
}
