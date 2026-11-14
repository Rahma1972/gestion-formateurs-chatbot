package tn.enicarthage.gestionformation.absence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.common.enums.StatutJustificatif;
import tn.enicarthage.gestionformation.presence.entity.PresenceSeance;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "justificatif_absence")
@Getter
@Setter
@NoArgsConstructor
public class JustificatifAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "presence_seance_id", nullable = false)
    private PresenceSeance presenceSeance;

    @Column(length = 1000)
    private String description;

    @Column(name = "nom_fichier")
    private String nomFichier;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @Column(name = "date_depot", nullable = false)
    private LocalDateTime dateDepot = LocalDateTime.now();

    @Column(name = "date_limite", nullable = false)
    private LocalDate dateLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutJustificatif statut = StatutJustificatif.EN_ATTENTE;

    @Column(name = "commentaire_admin", length = 500)
    private String commentaireAdmin;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;
}