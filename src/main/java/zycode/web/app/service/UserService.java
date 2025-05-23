package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zycode.web.app.dto.UserDto;
import zycode.web.app.entity.User;
import zycode.web.app.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     *
     * @param userDto The user data transfer object containing the user's details.
     * @return The registered user.
     */
    public User registerUser(UserDto userDto) {
        User user = mapToUser(userDto);
        return userRepository.save(user);
    }

    /**
     * Maps a UserDto to a User entity.
     *
     * @param dto The user data transfer object.
     * @return The mapped User entity.
     */
    private User mapToUser(UserDto dto) {
        return User.builder()
                .lastname(dto.getLastname())
                .firstname(dto.getFirstname())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .tag("zy_" + dto.getUsername())
                .dob(dto.getDob())
                .roles(List.of("USER"))
                .build();
    }

//    /**
//     * Authenticates a user and generates a JWT token.
//     *
//     * @param userDto The user data transfer object containing the user's credentials.
//     * @return A map containing the JWT token and the authenticated user.
//     * @throws UsernameNotFoundException If the user is not found.
//     */
//    public Map<String, Object> authenticateUser(UserDto userDto) {
//        Map<String, Object> authObject = new HashMap<String, Object>();
//        User user = (User) userDetailsService.loadUserByUsername(userDto.getUsername());
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        authenticationManager
//                .authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
//        authObject.put("token", "Bearer ".concat(jwtService.generateToken(userDto.getUsername())));
//        authObject.put("user", user);
//        return authObject;
//    }


}