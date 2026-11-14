package tn.enicarthage.gestionformation.salle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salle")
@Getter
@Setter
@NoArgsConstructor
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "اسم القاعة مطلوب")
    @Column(name = "nom", length = 50, nullable = false, unique = true)
    private String nom;

    @Column(nullable = true)
    private Integer capacite;
}