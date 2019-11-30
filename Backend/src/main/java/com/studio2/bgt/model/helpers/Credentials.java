package com.studio2.bgt.model.helpers;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Credentials {

    private String email;
    private String password;

}
