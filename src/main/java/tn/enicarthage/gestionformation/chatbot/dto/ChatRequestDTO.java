package tn.enicarthage.gestionformation.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {

    @NotBlank(message = "الرسالة لا يمكن أن تكون فارغة")
    private String message;

    private String historique; // conversation précédente, envoyée par le client
}