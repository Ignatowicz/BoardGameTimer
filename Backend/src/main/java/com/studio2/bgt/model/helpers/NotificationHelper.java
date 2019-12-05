package com.studio2.bgt.model.helpers;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHelper {

    Long playId;
    Map<String, String> players;
    Set<String> topics;

}
