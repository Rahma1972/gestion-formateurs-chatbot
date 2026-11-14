package tn.enicarthage.gestionformation.presence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenceDTO {
    private Long participantId;
    private String participantNom;
    private boolean present;
}