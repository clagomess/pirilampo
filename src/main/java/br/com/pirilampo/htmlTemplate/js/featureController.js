pirilampoApp.controller('featureController', function($scope, $route){
    var masterId = '[id="master_'+ $route.current.params.feature +'.html"]';

    var featureId = '[id="'+ $route.current.params.feature +'.feature"]';
    var featureMasterId = '[id="master_'+ $route.current.params.feature +'.feature"]';

    $('#feature-diff-row').hide();
    $('#feature-master').hide();

    if(jQuery(masterId).is('*')){
        jQuery('#feature-master').html(jQuery(masterId).html());

        $('body').prettyTextDiff({
            cleanup: true,
            originalContent: jQuery(featureMasterId).text(),
            changedContent: jQuery(featureId).text(),
            diffContainer: "#feature-diff"
        });

        jQuery('#btn-master').show();
        jQuery('#btn-diff').show();
    }else{
        jQuery('#btn-master').hide();
        jQuery('#btn-diff').hide();
    }
});