package it.unibz.infosec.examproject.user.application;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateUserDTO {

    private String email;
    private String password;
    private int userRole;
}
