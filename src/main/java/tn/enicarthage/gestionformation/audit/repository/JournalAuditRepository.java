package tn.enicarthage.gestionformation.audit.repository;

import tn.enicarthage.gestionformation.audit.entity.JournalAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalAuditRepository extends JpaRepository<JournalAudit, Long> {
    List<JournalAudit> findAllByOrderByDateEvenementDesc();
}