package tn.enicarthage.gestionformation.presence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.participant.entity.Participant;

import java.time.LocalDate;

@Entity
@Table(name = "presence_seance", uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "cycle_id", "date_seance"}))
@Getter
@Setter
@NoArgsConstructor
public class PresenceSeance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "cycle_id", nullable = false)
    private Cycle cycle;

    @Column(name = "date_seance", nullable = false)
    private LocalDate dateSeance;

    @Column(nullable = false)
    private boolean present = true;

    @Column(nullable = false)
    private boolean justifie = false;
}