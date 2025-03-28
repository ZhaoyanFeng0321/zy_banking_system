package zycode.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        var authObject = userService.authenticateUser(userDto);
        var token = (String) authObject.get("token");
        return ResponseEntity.ok()
                .header("Authorization", token)
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Authentication")
                .body(authObject.get("user"));
    }
}
