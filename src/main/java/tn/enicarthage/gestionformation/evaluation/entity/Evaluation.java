package tn.enicarthage.gestionformation.evaluation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.participant.entity.Participant;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation", uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "cycle_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "cycle_id", nullable = false)
    private Cycle cycle;

    @Column(nullable = false)
    private int note;

    @Column(length = 1000)
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false, updatable = false)
    private LocalDateTime dateEvaluation = LocalDateTime.now();
}