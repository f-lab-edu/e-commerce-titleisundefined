package hgk.ecommerce.domain.user.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.common.exceptions.DuplicatedException;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.dto.request.UserLoginDto;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.repository.UserRepository;
import hgk.ecommerce.global.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static hgk.ecommerce.global.utils.SessionUtils.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    @Transactional(readOnly = true)
    public void login(UserLoginDto userLoginDto) {
        User user = getUserByLoginId(userLoginDto);
        checkPassword(userLoginDto, user);
        checkStatus(user.getStatus());

        setUserSession(httpSession, user.getId());
    }

    @Override
    @Transactional
    public void signUp(UserSignUpDto userSignUpDto) {
        checkDuplicate(userSignUpDto);

        User user = User.createUser(userSignUpDto);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void signOut(User user) {
        User currentUser = getCurrentUserById(user.getId());
        currentUser.deleteUser();
    }

    @Override
    public void logout(User user) {
        httpSession.invalidate();
    }

    //region PRIVATE METHOD

    private User getCurrentUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new AuthorizationException("로그인 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private User getUserByLoginId(UserLoginDto userLoginDto) {
        return userRepository.findUserByLoginId(userLoginDto.getLoginId()).orElseThrow(() -> {
            throw new AuthorizationException("아이디를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private void checkDuplicate(UserSignUpDto userSignUpDto) {
        if(userRepository.existsUserByLoginId(userSignUpDto.getLoginId())) {
            throw new DuplicatedException("중복된 아이디가 존재합니다.", HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsUserByNickname(userSignUpDto.getNickname())) {
            throw new DuplicatedException("중복된 닉네임이 존재합니다", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkStatus(Status status) {
        switch (status) {
            case DELETE -> throw new AuthorizationException("탈퇴한 회원 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkPassword(UserLoginDto userLoginDto, User user) {
        boolean isEqual = user.getPassword().equals(userLoginDto.getPassword());

        if(!isEqual) {
            throw new AuthorizationException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //endregion
}
