package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j // 로그 찍기용 (log.info 같은거 사용 가능하게 함)
@Controller // 이 클래스는 Spring MVC 컨트롤러임을 선언
@RequiredArgsConstructor // final 필드를 가진 생성자 자동 생성 (DI 주입)
public class LoginController {

    private final LoginService loginService; // 로그인 비즈니스 로직을 담당하는 서비스 주입
    private final SessionManager sessionManager;

    @GetMapping("/login") // 로그인 폼 화면 요청
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        // 빈 LoginForm 객체를 모델에 담아서 loginForm.html로 전달
        return "login/loginForm"; // 로그인 폼 뷰 이름 반환
    }

    //@PostMapping("/login") // 로그인 폼 제출(POST) 처리
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        // 1. form 데이터 검증 (@NotEmpty 같은 애노테이션 확인)
        if (bindingResult.hasErrors()) {
            // 검증 실패 시 로그인 폼 다시 보여줌
            return "login/loginForm";
        }

        // 2. 실제 로그인 시도 (아이디, 비밀번호 검사)
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        // 3. 로그인 실패 처리
        if (loginMember == null) {
            // 글로벌 에러 등록 (필드 에러가 아닌, 전체 에러)
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 4. 로그인 성공 처리

        //쿠키에 시간 정보 안주면 세션 쿠키(브라우저 종료 시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId())); //값은 회원의 id를 담아둔다.
        response.addCookie(idCookie);
        return "redirect:/"; // 홈화면으로 리다이렉트

    }


    //@PostMapping("/login") // 로그인 폼 제출(POST) 처리
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        // 1. form 데이터 검증 (@NotEmpty 같은 애노테이션 확인)
        if (bindingResult.hasErrors()) {
            // 검증 실패 시 로그인 폼 다시 보여줌
            return "login/loginForm";
        }

        // 2. 실제 로그인 시도 (아이디, 비밀번호 검사)
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        // 3. 로그인 실패 처리
        if (loginMember == null) {
            // 글로벌 에러 등록 (필드 에러가 아닌, 전체 에러)
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 4. 로그인 성공 처리

        //세션 관리자를 통해 세션을 생성하고
        //쿠키에 시간 정보 안주면 세션 쿠키(브라우저 종료 시 모두 종료)
        sessionManager.createSession(loginMember, response);
        return "redirect:/"; // 홈화면으로 리다이렉트

    }

    @PostMapping("/login") // 로그인 폼 제출(POST) 처리
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        // 1. form 데이터 검증 (@NotEmpty 같은 애노테이션 확인)
        if (bindingResult.hasErrors()) {
            // 검증 실패 시 로그인 폼 다시 보여줌
            return "login/loginForm";
        }

        // 2. 실제 로그인 시도 (아이디, 비밀번호 검사)
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        // 3. 로그인 실패 처리
        if (loginMember == null) {
            // 글로벌 에러 등록 (필드 에러가 아닌, 전체 에러)
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 반환
        HttpSession session = request.getSession(); //false를 하면 세션 없을 경우 신규 세션 만들지 않고 null반환함
        //세션에 로그인 회원 정보 반환
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:/"; // 홈화면으로 리다이렉트

    }


    //5. 로그아웃 기능
    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }



    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
