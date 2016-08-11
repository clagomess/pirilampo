var pirilampoApp = angular.module('pirilampoApp', [
    'ngRoute',
    'ngResource'
]);

pirilampoApp.config(function($routeProvider){
    $routeProvider
        .when('/feature/:feature', {
            templateUrl: function(urlattr){
                return urlattr.feature + '.html';
            },
            controller: 'featureController'
        })
        .otherwise({
            redirectTo: '/'
        });
});
