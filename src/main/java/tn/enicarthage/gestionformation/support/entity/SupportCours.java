package tn.enicarthage.gestionformation.support.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_cours")
@Getter
@Setter
@NoArgsConstructor
public class SupportCours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String titre;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String url;

    @ManyToOne
    @JoinColumn(name = "cycle_id", nullable = false)
    private Cycle cycle;

    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout = LocalDateTime.now();
}