var mainModule = angular.module('mainModule', [ 'ui.router', 'ngStorage', 'angular-jwt' ]);

mainModule.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/pay/');

    $stateProvider.state('home', {
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

});