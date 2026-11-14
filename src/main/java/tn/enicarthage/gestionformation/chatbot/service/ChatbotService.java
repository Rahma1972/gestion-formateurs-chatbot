package tn.enicarthage.gestionformation.chatbot.service;

import org.springframework.stereotype.Service;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final OllamaClientService ollamaClientService;
    private final CycleRepository cycleRepository;
    private final InscriptionRepository inscriptionRepository;
    private final FiltreConfidentialiteService filtreConfidentialiteService;

    public ChatbotService(OllamaClientService ollamaClientService, CycleRepository cycleRepository,
                          InscriptionRepository inscriptionRepository,
                          FiltreConfidentialiteService filtreConfidentialiteService) {
        this.ollamaClientService = ollamaClientService;
        this.cycleRepository = cycleRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.filtreConfidentialiteService = filtreConfidentialiteService;
    }
    public String repondre(String messageUtilisateur, String historiqueConversation, Long participantId) {
        String langue = detecterLangue(messageUtilisateur);

        if (estQuestionSurInscription(messageUtilisateur)) {
            String contexteInscriptions = construireContexteInscriptions(participantId, langue);
            String prompt = construirePromptInscription(contexteInscriptions, messageUtilisateur, langue);
            return filtreConfidentialiteService.filtrer(ollamaClientService.genererReponse(prompt));
        }

        String contexteCycles = construireContexteCycles(langue);
        String contexteInscriptions = construireContexteInscriptions(participantId, langue);
        String promptComplet = construirePrompt(contexteCycles, contexteInscriptions, historiqueConversation, messageUtilisateur, langue);
        return filtreConfidentialiteService.filtrer(ollamaClientService.genererReponse(promptComplet));
    }

    private boolean estQuestionSurInscription(String message) {
        String texte = message.toLowerCase();
        return texte.contains("مسجل") || texte.contains("تسجيل") || texte.contains("inscrit")
                || texte.contains("inscription") || texte.contains("مسجلة");
    }

    private String construireContexteCycles(String langue) {
        List<Cycle> cycles = cycleRepository.findAll();
        boolean estArabe = "ar".equals(langue);

        if (cycles.isEmpty()) {
            return estArabe ? "لا توجد حاليا أي دورات تكوينية مسجلة في المنظومة."
                    : "Aucune formation n'est actuellement disponible sur la plateforme.";
        }

        if (estArabe) {
            return cycles.stream()
                    .map(c -> String.format(
                            "- %s (من %s إلى %s، القاعة: %s، المكونون: %s)",
                            c.getTheme(), c.getDateDeb(), c.getDateFin(),
                            c.getSalle() != null ? c.getSalle().getNom() : "غير محددة",
                            c.getFormateurs().stream().map(f -> f.getNomPrenom()).collect(Collectors.joining(", "))
                    ))
                    .collect(Collectors.joining("\n"));
        } else {
            return cycles.stream()
                    .map(c -> String.format(
                            "- %s (du %s au %s, salle : %s, formateurs : %s)",
                            c.getTheme(), c.getDateDeb(), c.getDateFin(),
                            c.getSalle() != null ? c.getSalle().getNom() : "non définie",
                            c.getFormateurs().stream().map(f -> f.getNomPrenom()).collect(Collectors.joining(", "))
                    ))
                    .collect(Collectors.joining("\n"));
        }
    }

    private String construireContexteInscriptions(Long participantId, String langue) {
        if (participantId == null) return "";

        var inscriptions = inscriptionRepository.findByParticipant_IdAndStatut(participantId, StatutInscription.ACTIVE);
        boolean estArabe = "ar".equals(langue);

        if (inscriptions.isEmpty()) {
            return estArabe ? "المستخدم الحالي غير مسجل حاليا في أي دورة تكوينية."
                    : "L'utilisateur actuel n'est inscrit à aucune formation actuellement.";
        }

        String prefixe = estArabe ? "المستخدم الحالي مسجل حاليا في: " : "L'utilisateur actuel est inscrit à : ";
        return prefixe + inscriptions.stream()
                .map(i -> i.getCycle().getTheme() + (estArabe ? " (من " : " (du ") + i.getCycle().getDateDeb()
                        + (estArabe ? " إلى " : " au ") + i.getCycle().getDateFin() + ")")
                .collect(Collectors.joining(", "));
    }

    private String construirePrompt(String contexteCycles, String contexteInscriptions, String historique,
                                    String messageUtilisateur, String langue) {
        if ("ar".equals(langue)) {
            return """
                أنت مساعد ذكي لمنصة تكوين تابعة للمركز الوطني للإعلامية.

                تعليمات صارمة:
                1. أجب حصريا باللغة العربية الفصحى، دون أي كلمة بالفرنسية أو الإنجليزية.
                2. اجعل إجابتك مختصرة: 3 إلى 5 جمل كحد أقصى، دون تكرار.
                3. إذا كانت قائمة الدورات طويلة، اقترح فقط الدورة أو الدورتين الأنسب لطلب المستخدم.
                4. لا تذكر أو تطلب معلومات شخصية حساسة.

                قائمة الدورات التكوينية المتوفرة حاليا:
                %s

                معلومات حول تسجيلات المستخدم الحالي:
                %s

                سجل المحادثة السابق (إن وجد):
                %s

                سؤال المستخدم الجديد: %s
                """.formatted(contexteCycles, contexteInscriptions, historique, messageUtilisateur);
        } else {
            return """
                Tu es un assistant intelligent pour la plateforme de formation du Centre National de l'Informatique.

                Instructions strictes :
                1. Réponds exclusivement en français, sans aucun mot arabe ou anglais.
                2. Sois concis : 3 à 5 phrases maximum, sans répétition.
                3. Si la liste des formations est longue, ne propose que 1 ou 2 formations les plus pertinentes.
                4. Ne mentionne ni ne demande jamais d'informations personnelles sensibles.

                Liste des formations actuellement disponibles :
                %s

                Informations sur les inscriptions actuelles de l'utilisateur :
                %s

                Historique de conversation précédent (le cas échéant) :
                %s

                Nouvelle question de l'utilisateur : %s
                """.formatted(contexteCycles, contexteInscriptions, historique, messageUtilisateur);
        }
    }
    public String genererDescriptionCycle(String theme, String motsClefs) {
        String prompt = """
            أنت مساعد يساعد إداري مركز تكوين على كتابة وصف جذاب ومهني لدورة تكوينية.
            اكتب وصفا قصيرا (3 إلى 4 جمل) لدورة تكوينية بعنوان: "%s"
            الكلمات المفتاحية أو الأهداف المرتبطة بها: %s

            اكتب الوصف مباشرة دون أي مقدمة أو تعليق إضافي.
            """.formatted(theme, motsClefs);
        return ollamaClientService.genererReponse(prompt);
    }

    private String detecterLangue(String message) {
        // Compte les caractères arabes vs latins pour déterminer la langue dominante
        long caracteresArabes = message.chars().filter(c -> c >= 0x0600 && c <= 0x06FF).count();
        long caracteresLatins = message.chars().filter(c -> Character.isLetter(c) && c < 0x0600).count();
        return caracteresArabes >= caracteresLatins ? "ar" : "fr";
    }

    private String construirePromptInscription(String contexteInscriptions, String messageUtilisateur, String langue) {
        if ("ar".equals(langue)) {
            return """
                أنت مساعد لمنصة تكوين. أجب باللغة العربية فقط، بجملة أو جملتين فقط، على السؤال التالي
                باستعمال حصريا المعلومة أدناه، دون اقتراح أي دورة تكوينية جديدة.

                معلومة عن تسجيلات المستخدم: %s

                سؤال المستخدم: %s
                """.formatted(contexteInscriptions, messageUtilisateur);
        } else {
            return """
                Tu es un assistant pour une plateforme de formation. Réponds exclusivement en français,
                en une ou deux phrases maximum, en utilisant uniquement l'information ci-dessous,
                sans proposer de nouvelle formation.

                Information sur les inscriptions de l'utilisateur : %s

                Question de l'utilisateur : %s
                """.formatted(contexteInscriptions, messageUtilisateur);
        }
    }

    public String analyserCv(String texteCv, String langue) {
        String contexteCycles = construireContexteCycles(langue);
        String texteTronque = texteCv.length() > 3000 ? texteCv.substring(0, 3000) : texteCv;
        String prompt = construirePromptAnalyseCv(texteTronque, contexteCycles, langue);
        return filtreConfidentialiteService.filtrer(ollamaClientService.genererReponse(prompt));
    }

    private String construirePromptAnalyseCv(String texteCv, String contexteCycles, String langue) {
        if ("ar".equals(langue)) {
            return """
                أنت مساعد ذكي لمنصة تكوين. حلل السيرة الذاتية التالية للمستخدم،
                واستنتج منها تخصصه ومهاراته الرئيسية، ثم اقترح له دورة أو دورتين تكوينيتين
                من القائمة أدناه تتناسب مع ملفه.

                تعليمات صارمة:
                1. أجب حصريا باللغة العربية الفصحى.
                2. كن مختصرا: فقرة واحدة لتلخيص الملف، وفقرة لاقتراح الدورات.
                3. لا تعد نص السيرة الذاتية كاملا، فقط استنتاجاتك.
                4. لا تذكر أي معلومة شخصية حساسة وردت في السيرة (هاتف، عنوان، بريد إلكتروني).
                5. اكتب اسم كل دورة تقترحها بالضبط كما ورد في القائمة أدناه، دون ترجمته أو تعديله.

                نص السيرة الذاتية:
                %s

                الدورات التكوينية المتوفرة حاليا:
                %s
                """.formatted(texteCv, contexteCycles);
        } else {
            return """
            Tu es un assistant intelligent pour une plateforme de formation. Analyse le CV suivant,
            déduis-en le profil et les compétences principales de la personne, puis recommande-lui
            une ou deux formations parmi la liste ci-dessous adaptées à son profil.

            Instructions strictes :
            1. Réponds exclusivement en français.
            2. Sois concis : un paragraphe de résumé du profil, un paragraphe de recommandation.
            3. Ne recopie pas le texte du CV, donne uniquement tes conclusions.
            4. Ne mentionne aucune information personnelle sensible présente dans le CV (téléphone, adresse, email).
            5. Reproduis EXACTEMENT le nom de chaque formation recommandée tel qu'il apparaît dans la liste ci-dessous, sans le traduire ni le modifier.

            Texte du CV :
            %s

            Formations actuellement disponibles :
            %s
            """.formatted(texteCv, contexteCycles);
        }
    }
}