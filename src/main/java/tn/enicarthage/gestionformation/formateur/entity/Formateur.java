package tn.enicarthage.gestionformation.formateur.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;

@Entity
@Table(name = "formateur")
@Getter
@Setter
@NoArgsConstructor
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "الإسم و اللقب مطلوب")
    @Column(name = "nom_prenom", length = 50, nullable = false)
    private String nomPrenom;

    @NotBlank(message = "الإختصاص مطلوب")
    @Column(length = 150, nullable = false)
    private String specialite;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String direction;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String entreprise;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "id", unique = true)
    private Utilisateur compte;
}