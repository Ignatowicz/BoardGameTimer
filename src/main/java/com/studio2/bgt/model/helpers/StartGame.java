package com.studio2.bgt.model.helpers;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StartGame {

    private Long playId;
    private Set<Long> playersId;

}
