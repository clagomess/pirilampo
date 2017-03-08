pirilampoApp.controller('featureController', function($scope, $route){
    var masterId = '[id="master_'+ $route.current.params.feature +'.html"]';

    var featureId = '[id="'+ $route.current.params.feature +'.feature"]';
    var featureMasterId = '[id="master_'+ $route.current.params.feature +'.feature"]';

    jQuery('#feature-diff-row').hide();
    jQuery('#feature-master').hide();

    if(jQuery(masterId).is('*')){
        jQuery('#feature-master').html(jQuery(masterId).html());
        jQuery('#feature-master').prepend('<span class="master-label">MASTER</span>');

        jQuery('body').prettyTextDiff({
            cleanup: true,
            originalContent: jQuery(featureMasterId).text(),
            changedContent: jQuery(featureId).text(),
            diffContainer: "#feature-diff"
        });

        jQuery('#btn-master').show();
        jQuery('#btn-diff').show();
        jQuery('[ng-view]').css('background', '#dff0d8');
    }else{
        jQuery('#btn-master').hide();
        jQuery('#btn-diff').hide();
        jQuery('[ng-view]').css('background', '');
    }

    // Quando for busca
    if($route.current.params.search){
        var html = jQuery('[ng-view]').html();

        var re = new RegExp($route.current.params.search, 'gm');

        html = html.replace(
            re,
            '<span class="searched">'+$route.current.params.search+'</span>'
        );

        jQuery('[ng-view]').html(html);

        jQuery('html, body').animate({
            scrollTop: jQuery('.searched').offset().top
        }, 500);
    }

    // Quando for scenario
    if($route.current.params.scenarioid){
        jQuery('html, body').animate({
            scrollTop: jQuery('[data-target="#scenario-'+$route.current.params.scenarioid+'"]').offset().top
        }, 500);
    }

    // Se houver link para scenario, adicionar opção de pop-up
    var modalTemplate = Handlebars.compile(jQuery('[id="layout-modal.html"]').html().trim());

    jQuery('[href^="#/scenario"]').click(function(){
        var matches = jQuery(this).attr('href').match(/#\/scenario\/(.+)\/(\d+)/);

        if(matches.length == 3) {
            var html = jQuery('[id="' + matches[1] + '.html"]').html();

            if(html) {
                jQuery('#modal .modal-content *').remove();

                jQuery("#modal .modal-content").html(modalTemplate({
                    title: jQuery(html).find('[data-target="#scenario-' + matches[2] + '"]').text(),
                    body: jQuery(html).find('#scenario-' + matches[2]).html().trim(),
                    href: matches[0]
                }));

                jQuery('#modal').modal('show');

                return false;
            }
        }
    });

    // fancyfox
    jQuery('div[ng-view] img[src^="data"]').each(function(){
        if(!jQuery(this).hasClass('logo')) {
            var html = '<a href="' + jQuery(this).attr('src') + '" class="fancybox">';
            html += jQuery(this).parent().html();
            html += '</a>';

            jQuery(this).replaceWith(html);
        }
    });

    jQuery(".fancybox").fancybox();
});

pirilampoApp.controller('indexController', function($scope){
    jQuery('#feature-diff-row').hide();
    jQuery('#feature-master').hide();
    jQuery('#btn-master').hide();
    jQuery('#btn-diff').hide();
});

pirilampoApp.controller('htmlController', function($scope, $route){
    jQuery('#feature-diff-row').hide();
    jQuery('#feature-master').hide();
    jQuery('#btn-master').hide();
    jQuery('#btn-diff').hide();

    var fnResize = function(){
        // 90 - equivalente aos padding
        jQuery('#html-frame').height(jQuery(window).height() - 170);
    };

    fnResize();

    jQuery(window).resize(function () {
        fnResize();
    });

    // monta conteúdo
    jQuery('#html-frame-title').text($route.current.params.html);

    var blob = new Blob([jQuery('[id="'+$route.current.params.html+'"]').html()], {type:'text/html'});
    document.querySelector('iframe').src = URL.createObjectURL(blob);
});