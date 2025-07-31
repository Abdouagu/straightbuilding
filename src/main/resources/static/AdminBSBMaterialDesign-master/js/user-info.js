// user-info.js - À placer dans static/AdminBSBMaterialDesign-master/js/

// Fonction pour charger les informations de l'utilisateur connecté
async function loadUserInfo() {
    try {
        const response = await fetch('/api/current-user', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'same-origin'
        });

        if (response.ok) {
            const userInfo = await response.json();
            updateUserDisplay(userInfo);
        } else {
            console.error('Erreur lors du chargement des informations utilisateur');
            // Rediriger vers login si non authentifié
            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            }
        }
    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Fonction pour mettre à jour l'affichage des informations utilisateur
function updateUserDisplay(userInfo) {
    // Mettre à jour le nom d'utilisateur
    const nameElement = document.querySelector('.appUser-info .name');
    if (nameElement) {
        nameElement.textContent = userInfo.username || 'Utilisateur';
    }

    // Mettre à jour l'email
    const emailElement = document.querySelector('.appUser-info .email');
    if (emailElement) {
        emailElement.textContent = userInfo.email || 'email@example.com';
    }

    // Optionnel : mettre à jour d'autres éléments selon le rôle
    console.log('Utilisateur connecté:', userInfo);
}

// Fonction pour gérer la déconnexion
function logout() {
    // Créer un formulaire de déconnexion et le soumettre
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/logout';

    // Ajouter le token CSRF si nécessaire (dans votre cas CSRF est désactivé)
    document.body.appendChild(form);
    form.submit();
}

// Charger les informations utilisateur quand la page est prête
document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo();

    // Ajouter l'événement de déconnexion - chercher le lien par son texte
    const signOutLinks = document.querySelectorAll('a');
    signOutLinks.forEach(link => {
        if (link.textContent.includes('Sign Out') || link.textContent.includes('Déconnexion')) {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                logout();
            });
        }
    });
});