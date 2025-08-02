$(function () {
    skinChanger();
    activateNotificationAndTasksScroll();

    setSkinListHeightAndScroll(true);
    setSettingListHeightAndScroll(true);
    $(window).resize(function () {
        setSkinListHeightAndScroll(false);
        setSettingListHeightAndScroll(false);
    });
});

//Skin changer
function skinChanger() {
    $('.right-sidebar .demo-choose-skin li').on('click', function () {
        var $body = $('body');
        var $this = $(this);

        var existTheme = $('.right-sidebar .demo-choose-skin li.active').data('theme');
        $('.right-sidebar .demo-choose-skin li').removeClass('active');
        $body.removeClass('theme-' + existTheme);
        $this.addClass('active');

        $body.addClass('theme-' + $this.data('theme'));
    });
}

//Skin tab content set height and show scroll - MODIFIÉ
function setSkinListHeightAndScroll(isFirstTime) {
    var height = $(window).height() - ($('.navbar').innerHeight() + $('.right-sidebar .nav-tabs').outerHeight());
    var $el = $('.demo-choose-skin');

    if (!isFirstTime){
        $el.slimScroll({ destroy: true }).height('auto');
        $el.parent().find('.slimScrollBar, .slimScrollRail').remove();
    }

    $el.slimscroll({
        height: height + 'px',
        color: 'rgba(0,0,0,0.5)',
        size: '6px',
        alwaysVisible: true,               // MODIFIÉ - de false à true
        disableFadeOut: true,              // AJOUTÉ - Empêche la disparition
        railVisible: true,                 // AJOUTÉ - Montre le rail
        railColor: '#ddd',                 // AJOUTÉ - Couleur du rail
        railOpacity: 0.2,                  // AJOUTÉ - Opacité du rail
        wheelStep: 20,                     // AJOUTÉ - Vitesse de scroll
        allowPageScroll: false,            // AJOUTÉ - Empêche le scroll de la page
        borderRadius: '0',
        railBorderRadius: '0'
    });
}

//Setting tab content set height and show scroll - MODIFIÉ
function setSettingListHeightAndScroll(isFirstTime) {
    var height = $(window).height() - ($('.navbar').innerHeight() + $('.right-sidebar .nav-tabs').outerHeight());
    var $el = $('.right-sidebar .demo-settings');

    if (!isFirstTime){
        $el.slimScroll({ destroy: true }).height('auto');
        $el.parent().find('.slimScrollBar, .slimScrollRail').remove();
    }

    $el.slimscroll({
        height: height + 'px',
        color: 'rgba(0,0,0,0.5)',
        size: '6px',
        alwaysVisible: true,               // MODIFIÉ - de false à true
        disableFadeOut: true,              // AJOUTÉ - Empêche la disparition
        railVisible: true,                 // AJOUTÉ - Montre le rail
        railColor: '#ddd',                 // AJOUTÉ - Couleur du rail
        railOpacity: 0.2,                  // AJOUTÉ - Opacité du rail
        wheelStep: 20,                     // AJOUTÉ - Vitesse de scroll
        allowPageScroll: false,            // AJOUTÉ - Empêche le scroll de la page
        borderRadius: '0',
        railBorderRadius: '0'
    });
}

//Activate notification and task dropdown on top right menu - MODIFIÉ
function activateNotificationAndTasksScroll() {
    $('.navbar-right .dropdown-menu .body .menu').slimscroll({
        height: '254px',
        color: 'rgba(0,0,0,0.5)',
        size: '4px',
        alwaysVisible: true,               // MODIFIÉ - de false à true
        disableFadeOut: true,              // AJOUTÉ - Empêche la disparition
        railVisible: true,                 // AJOUTÉ - Montre le rail
        railColor: '#ddd',                 // AJOUTÉ - Couleur du rail
        railOpacity: 0.2,                  // AJOUTÉ - Opacité du rail
        wheelStep: 20,                     // AJOUTÉ - Vitesse de scroll
        allowPageScroll: false,            // AJOUTÉ - Empêche le scroll de la page
        borderRadius: '0',
        railBorderRadius: '0'
    });
}

// CORRECTION SUPPLÉMENTAIRE - Force la persistance des scrollbars
$(document).ready(function() {
    // Attendre que tous les éléments soient chargés
    setTimeout(function() {
        // Réappliquer les options de scrollbar après chargement complet
        $('.demo-choose-skin, .right-sidebar .demo-settings, .navbar-right .dropdown-menu .body .menu').each(function() {
            var $this = $(this);
            if ($this.parent().hasClass('slimScrollDiv')) {
                // Détruire et recréer avec les bonnes options
                $this.slimScroll({destroy: true});

                setTimeout(function() {
                    var height = $this.hasClass('demo-choose-skin') || $this.hasClass('demo-settings')
                        ? $(window).height() - ($('.navbar').innerHeight() + $('.right-sidebar .nav-tabs').outerHeight()) + 'px'
                        : '254px';

                    var size = $this.hasClass('menu') ? '4px' : '6px';

                    $this.slimScroll({
                        height: height,
                        color: 'rgba(0,0,0,0.5)',
                        size: size,
                        alwaysVisible: true,
                        disableFadeOut: true,
                        railVisible: true,
                        railColor: '#ddd',
                        railOpacity: 0.2,
                        wheelStep: 20,
                        allowPageScroll: false,
                        borderRadius: '0',
                        railBorderRadius: '0'
                    });
                }, 100);
            }
        });
    }, 1500);
});

//Google Analytics ======================================================================================
addLoadEvent(loadTracking);
var trackingId = 'UA-30038099-6';

function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function () {
            oldonload();
            func();
        }
    }
}

function loadTracking() {
    (function (i, s, o, g, r, a, m) {
        i['GoogleAnalyticsObject'] = r; i[r] = i[r] || function () {
            (i[r].q = i[r].q || []).push(arguments)
        }, i[r].l = 1 * new Date(); a = s.createElement(o),
            m = s.getElementsByTagName(o)[0]; a.async = 1; a.src = g; m.parentNode.insertBefore(a, m)
    })(window, document, 'script', 'https://www.google-analytics.com/analytics.js', 'ga');

    ga('create', trackingId, 'auto');
    ga('send', 'pageview');
}
//========================================================================================================