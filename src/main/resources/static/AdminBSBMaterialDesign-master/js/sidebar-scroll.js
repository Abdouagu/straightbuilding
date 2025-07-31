/* Active la barre de scroll vertical sur la sidebar gauche */
$(function () {
    $('#leftsidebar .menu').slimScroll({
        height: 'calc(100vh - 140px)', // espace disponible sous le profil
        wheelStep: 10,
        touchScrollStep: 50
    });
});