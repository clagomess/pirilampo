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
});

pirilampoApp.controller('indexController', function($scope){
    jQuery('#feature-diff-row').hide();
    jQuery('#feature-master').hide();
    jQuery('#btn-master').hide();
    jQuery('#btn-diff').hide();
});