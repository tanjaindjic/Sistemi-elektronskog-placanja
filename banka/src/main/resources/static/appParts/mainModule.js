const ROOT_PATH = "http://localhost:8082/";

var mainModule = angular.module('mainModule', [ 'ui.router', 'ngStorage', 'angular-jwt' ]);
/*
mainModule.config(['$locationProvider', function($locationProvider) {
             $locationProvider.html5Mode({
                    enabled: true,
                    requireBase: false
             });
           }]);
*/

mainModule.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/404');

    $stateProvider.state('pay', {
        url: '/pay/{token}',
        templateUrl : 'appParts/centerComponent/center.html',
    })
    .state('expired', {
        url: '/expired',
        templateUrl: 'appParts/expiredComponent/expired.html',
        controller : 'expiredController'
    })
    .state('paymentSent', {
        url: '/paymentSent',
        templateUrl: 'appParts/paymentSentComponent/paymentSent.html',
        controller : 'paymentSentController'
    })
    .state('success', {
        url: '/success',
        templateUrl: 'appParts/successComponent/success.html',
        controller : 'successController'
    })
    .state('failed', {
        url: '/failed',
        templateUrl: 'appParts/failedComponent/failed.html',
        controller : 'failedController'
    })
    .state('404', {
        url: '/404',
        templateUrl: 'appParts/404Component/404.html',
        controller : '404Controller'
    })
});