package tn.enicarthage.gestionformation.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import tn.enicarthage.gestionformation.audit.service.AuditService;
import tn.enicarthage.gestionformation.common.enums.TypeEvenement;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import java.time.LocalDateTime;

@Component
public class LoginAttemptListener {

    private static final int MAX_TENTATIVES = 5;
    private static final int DUREE_VERROUILLAGE_MINUTES = 15;

    private final UtilisateurRepository utilisateurRepository;
    private final AuditService auditService;

    public LoginAttemptListener(UtilisateurRepository utilisateurRepository, AuditService auditService) {
        this.utilisateurRepository = utilisateurRepository;
        this.auditService = auditService;
    }

    @EventListener
    public void gererEchecConnexion(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();
        auditService.enregistrer(TypeEvenement.CONNEXION_ECHOUEE, email, "محاولة دخول فاشلة");

        utilisateurRepository.findByEmail(email).ifPresent(utilisateur -> {
            int tentatives = utilisateur.getTentativesEchouees() + 1;
            utilisateur.setTentativesEchouees(tentatives);

            if (tentatives >= MAX_TENTATIVES) {
                utilisateur.setVerrouilleJusqua(LocalDateTime.now().plusMinutes(DUREE_VERROUILLAGE_MINUTES));
                auditService.enregistrer(TypeEvenement.COMPTE_VERROUILLE, email,
                        "تم قفل الحساب بعد " + MAX_TENTATIVES + " محاولات فاشلة");
            }
            utilisateurRepository.save(utilisateur);
        });
    }

    @EventListener
    public void gererConnexionReussie(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        auditService.enregistrer(TypeEvenement.CONNEXION_REUSSIE, email, "تسجيل دخول ناجح");

        utilisateurRepository.findByEmail(email).ifPresent(utilisateur -> {
            if (utilisateur.getTentativesEchouees() > 0 || utilisateur.getVerrouilleJusqua() != null) {
                utilisateur.setTentativesEchouees(0);
                utilisateur.setVerrouilleJusqua(null);
                utilisateurRepository.save(utilisateur);
            }
        });
    }
}