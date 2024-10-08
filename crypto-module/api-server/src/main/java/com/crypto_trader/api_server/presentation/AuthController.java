package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.AuthService;
import com.crypto_trader.api_server.auth.PrincipalUser;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username) {
        return authService.login(username);
    }

    @GetMapping("/auth-check")
    public String checkAuthUser(@AuthenticationPrincipal PrincipalUser principalUser,
                                @AuthenticationPrincipal(expression = "user") UserEntity user) {
        System.out.println(principalUser);
        System.out.println(user);
        return user.getUsername();
    }

    @PostMapping("/signup")
    public String signup(String username) {
        authService.signup(username);
        return "success";
    }
}
