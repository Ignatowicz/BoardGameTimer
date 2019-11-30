package com.studio2.bgt.model.entity;

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
