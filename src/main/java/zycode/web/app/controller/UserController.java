package zycode.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import zycode.web.app.dto.UserDto;
import zycode.web.app.entity.User;
import zycode.web.app.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        try {
            User user = userService.registerUser(userDto);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something wrong. Failed to register.");
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        try {
            var authObject = userService.authenticateUser(userDto);
            var token = (String) authObject.get("token");
            return ResponseEntity.ok()
                    .header("Authorization", token)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Authorization")
                    .body(authObject.get("user"));
        } catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something wrong. Failed to login.");

        }
    }
}
