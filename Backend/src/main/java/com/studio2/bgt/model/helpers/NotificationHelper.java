package com.studio2.bgt.model.helpers;

import com.studio2.bgt.model.entity.Player;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHelper {

    Player player;
    PlayHelper play;
    Map<String, String> players;
    Set<String> topics;

}
