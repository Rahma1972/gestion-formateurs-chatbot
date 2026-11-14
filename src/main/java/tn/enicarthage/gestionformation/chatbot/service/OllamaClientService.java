package tn.enicarthage.gestionformation.chatbot.service;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaClientService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODELE = "llama3.2";
    private static final int TIMEOUT_MS = 45_000;

    private final RestClient restClient;

    public OllamaClientService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MS);
        factory.setReadTimeout(TIMEOUT_MS);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public String genererReponse(String prompt) {
        try {
            Map<String, Object> corpsRequete = Map.of(
                    "model", MODELE,
                    "prompt", prompt,
                    "stream", false
            );

            Map<String, Object> reponse = restClient.post()
                    .uri(OLLAMA_URL)
                    .body(corpsRequete)
                    .retrieve()
                    .body(Map.class);

            if (reponse != null && reponse.get("response") != null) {
                return reponse.get("response").toString();
            }
            return "عذرا، لم أتمكن من الحصول على إجابة.";

        } catch (org.springframework.web.client.ResourceAccessException e) {
            return "انتهت مهلة الانتظار. الرجاء إعادة المحاولة أو التأكد من أن Ollama يعمل بشكل صحيح على جهازك.";
        } catch (Exception e) {
            return "الخدمة غير متوفرة حاليا. تأكد من أن Ollama يعمل على جهازك.";
        }
    }
}