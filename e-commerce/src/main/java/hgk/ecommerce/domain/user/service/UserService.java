package hgk.ecommerce.domain.user.service;

import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.dto.request.UserLoginDto;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;

public interface UserService {
    void login(UserLoginDto userLoginDto);
    void signUp(UserSignUpDto userSignUpDto);
    void signOut(Long userId);
    void logout(Long userId);
}
