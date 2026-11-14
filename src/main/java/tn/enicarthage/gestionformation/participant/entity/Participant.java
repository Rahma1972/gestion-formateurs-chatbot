package tn.enicarthage.gestionformation.participant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;

@Entity
@Table(name = "participant")
@Getter
@Setter
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "الإسم و اللقب مطلوب")
    @Column(name = "nom_prenom", length = 50, nullable = false)
    private String nomPrenom;

    @NotBlank(message = "بطاقة التعريف الوطنية مطلوبة")
    @Column(length = 20, nullable = false, unique = true)
    private String cin;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String entreprise;

    @Column(name = "tel_fix", length = 20)
    private String telFix;

    @Column(length = 20)
    private String fax;

    @Column(name = "tel_port", length = 20)
    private String telPort;

    @Column(length = 100)
    private String mail;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "id", unique = true)
    private Utilisateur compte;
}