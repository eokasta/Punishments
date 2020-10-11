package com.github.eokasta.legionbans.punishment;

import lombok.Data;

@Data
public class Punishment {

    private final String player;
    private final PunishmentType type;
    private final String reason, author;
    private final long expiresAt, appliedAt;
    private transient int id;
    private boolean active;

}
