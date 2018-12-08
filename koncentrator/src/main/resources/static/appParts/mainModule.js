const ROOT_PATH = "/paymentGateway/rest/";

var mainModule = angular.module('mainModule', [ 'ui.router', 'ngStorage', 'angular-jwt' ]);

mainModule.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider.state('home', {
        url: '/home',
        templateUrl : 'appParts/welcomePage/welcomePage.html'
    })
    .state('payment', {
        url: '/payment/{token}',
        templateUrl : 'appParts/mainPanel/mainPanel.html',
        controller : 'mainPanelController'
    })

});