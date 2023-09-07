package hgk.ecommerce.domain.user.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.AuthorizationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import hgk.ecommerce.domain.user.dto.request.UserLogin;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.repository.UserRepository;
import hgk.ecommerce.global.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Transactional(readOnly = true)
    public void login(UserLogin userLogin) {
        User user = userRepository.findUserByLoginId(userLogin.getLoginId()).orElseThrow(() -> {
            throw new NoResourceException("존재하지 않는 아이디입니다.", BAD_REQUEST);
        });

        checkPassword(userLogin.getPassword(), user.getPassword());
        checkStatus(user.getStatus());

        SessionUtils.setSession(httpSession, user.getId(), USER);
    }

    @Transactional
    public void signUp(UserSign userSign) {
        checkDuplicateLoginId(userSign.getLoginId());
        checkDuplicateNickname(userSign.getNickname());

        User user = User.createUser(userSign);

        userRepository.save(user);
    }

    @Transactional
    public void delete(User user) {

        userRepository.findById(user.getId()).ifPresent((foundUser) -> {
            foundUser.deleteUser();
        });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Long userId = SessionUtils.getSession(httpSession, USER);

        return userRepository.findById(userId).orElseThrow(() -> {
            throw new AuthorizationException("로그인 후에 진행해주세요.", BAD_REQUEST);
        });
    }

    //region PRIVATE METHOD
    private void checkDuplicateLoginId(String loginId) {
        if (userRepository.existsUserByLoginId(loginId) == true) {
            throw new AlreadyExistException("이미 존재하는 아이디 입니다.", BAD_REQUEST);
        }
    }

    private void checkDuplicateNickname(String nickname) {
        if (userRepository.existsUserByNickname(nickname) == true) {
            throw new AlreadyExistException("이미 존재하는 닉네임 입니다.", BAD_REQUEST);
        }
    }

    private void checkPassword(String plainText, String password) {
        if(!plainText.equals(password)) {
            throw new AuthorizationException("비밀번호가 일치하지 않습니다.", BAD_REQUEST);
        }
    }

    private void checkStatus(Status status) {
        switch (status) {
            case DELETED -> throw new AuthenticationException("이미 탈퇴한 회원입니다.", BAD_REQUEST);
        }
    }
    //endregion
}
