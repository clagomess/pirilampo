var pirilampoApp = angular.module('pirilampoApp', [
    'ngRoute',
    'ngResource'
]);

pirilampoApp.config(function($routeProvider){
    $routeProvider
        .when('/feature/:feature/:search*?', {
            templateUrl: function(urlattr){
                return urlattr.feature + '.html';
            },
            controller: 'featureController'
        })
        .when('/scenario/:feature/:scenarioid', {
            templateUrl: function(urlattr){
                return urlattr.feature + '.html';
            },
            controller: 'featureController'
        })
        .when('/', {
            templateUrl: 'index.html',
            controller: 'indexController'
        })
        .otherwise({
            redirectTo: '/'
        });
});
