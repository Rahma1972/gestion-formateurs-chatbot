package tn.enicarthage.gestionformation.audit.service;

import org.springframework.stereotype.Service;
import tn.enicarthage.gestionformation.audit.entity.JournalAudit;
import tn.enicarthage.gestionformation.audit.repository.JournalAuditRepository;
import tn.enicarthage.gestionformation.common.enums.TypeEvenement;

import java.util.List;

@Service
public class AuditService {

    private final JournalAuditRepository journalAuditRepository;

    public AuditService(JournalAuditRepository journalAuditRepository) {
        this.journalAuditRepository = journalAuditRepository;
    }

    public void enregistrer(TypeEvenement type, String email, String details) {
        JournalAudit entree = new JournalAudit();
        entree.setTypeEvenement(type);
        entree.setEmailConcerne(email);
        entree.setDetails(details);
        journalAuditRepository.save(entree);
    }

    public List<JournalAudit> listerTout() {
        return journalAuditRepository.findAllByOrderByDateEvenementDesc();
    }
}