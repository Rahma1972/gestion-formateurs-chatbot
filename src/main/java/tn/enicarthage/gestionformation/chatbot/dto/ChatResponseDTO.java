package tn.enicarthage.gestionformation.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponseDTO {

    private String reponse;
    private String messageUtilisateurFiltre;

    public ChatResponseDTO(String reponse) {
        this.reponse = reponse;
    }

    public ChatResponseDTO(String reponse, String messageUtilisateurFiltre) {
        this.reponse = reponse;
        this.messageUtilisateurFiltre = messageUtilisateurFiltre;
    }
}