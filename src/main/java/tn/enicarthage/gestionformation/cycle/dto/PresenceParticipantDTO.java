package tn.enicarthage.gestionformation.cycle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenceParticipantDTO {
    private Long participantId;
    private String participantNom;
    private boolean present;
    private long totalAbsencesCycle;
}