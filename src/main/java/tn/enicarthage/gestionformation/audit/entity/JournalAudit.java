package tn.enicarthage.gestionformation.audit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.enicarthage.gestionformation.common.enums.TypeEvenement;

import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
@Getter
@Setter
@NoArgsConstructor
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeEvenement typeEvenement;

    @Column(length = 100)
    private String emailConcerne;

    @Column(length = 255)
    private String details;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateEvenement = LocalDateTime.now();
}