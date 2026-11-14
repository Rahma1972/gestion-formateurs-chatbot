let historiqueConversation = "";

function ajouterMessage(texte, classe) {
    const zone = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'message ' + classe;
    div.textContent = texte;
    zone.appendChild(div);
    zone.scrollTop = zone.scrollHeight;
}

async function envoyerMessage() {
    const input = document.getElementById('messageInput');
    const texte = input.value.trim();
    if (!texte) return;

    input.value = '';

    const zone = document.getElementById('chatMessages');
    const chargement = document.createElement('div');
    chargement.className = 'message message-bot chargement';
    chargement.id = 'chargementMsg';
    chargement.textContent = 'جاري الكتابة...';
    zone.appendChild(chargement);
    zone.scrollTop = zone.scrollHeight;

    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const headers = { 'Content-Type': 'application/json' };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const reponse = await fetch('/chatbot/message', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({ message: texte, historique: historiqueConversation })
        });

        const data = await reponse.json();
        document.getElementById('chargementMsg').remove();

        // Affiche le message utilisateur APRÈS filtrage côté serveur, jamais le texte brut original
        ajouterMessage(data.messageUtilisateurFiltre, 'message-utilisateur');
        ajouterMessage(data.reponse, 'message-bot');

        // Mise à jour de l'historique local avec la version déjà filtrée
        historiqueConversation += "\nUser: " + data.messageUtilisateurFiltre + "\nAssistant: " + data.reponse;
        const lignes = historiqueConversation.split("\n").filter(l => l.trim() !== "");
        if (lignes.length > 12) {
            historiqueConversation = lignes.slice(-12).join("\n");
        }

    } catch (erreur) {
        document.getElementById('chargementMsg').remove();
        ajouterMessage('حدث خطأ في الاتصال بالمساعد.', 'message-bot');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('messageInput').addEventListener('keypress', (event) => {
        if (event.key === 'Enter') envoyerMessage();
    });
    document.getElementById('envoyerBtn').addEventListener('click', envoyerMessage);
});