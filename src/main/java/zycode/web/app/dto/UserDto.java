package zycode.web.app.dto;

import jdk.jshell.Snippet;
import lombok.*;

import java.util.Date;

/**
 * This class represents a User's data transfer object (DTO). It is used to transfer user information between layers of an application.
 *
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    private String firstname;
    private String lastname;
    private String username;

    private Date dob;
    private long tel;
    private String password;
    private String gender;

}
