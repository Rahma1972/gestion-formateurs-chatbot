package tn.enicarthage.gestionformation.cycle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.common.enums.StatutCycle;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.salle.entity.Salle;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cycle")
@Getter
@Setter
@NoArgsConstructor
public class Cycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "num_act", length = 20, nullable = false)
    private String numAct;

    @NotBlank(message = "الدورة التكوينية مطلوبة")
    @Column(length = 100, nullable = false)
    private String theme;

    @NotNull(message = "تاريخ البداية مطلوب")
    @Column(name = "date_deb", nullable = false)
    private LocalDate dateDeb;

    @NotNull(message = "تاريخ النهاية مطلوب")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCycle statut = StatutCycle.PLANIFIE;

    @ManyToOne
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @ManyToMany
    @JoinTable(
            name = "cycle_formateur",
            joinColumns = @JoinColumn(name = "cycle_id"),
            inverseJoinColumns = @JoinColumn(name = "formateur_id")
    )
    private Set<Formateur> formateurs = new HashSet<>();
}