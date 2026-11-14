package tn.enicarthage.gestionformation.cycle.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SeanceDTO {
    private LocalDate date;
    private List<PresenceParticipantDTO> presences;
}