var mainModule = angular.module('mainModule', [ 'ui.router', 'ngStorage', 'angular-jwt' ]);

mainModule.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider.state('home', {
        url: '/home',
        templateUrl : 'appParts/centerComponent/center.html',
    })
    .state('login', {
        url: '/login',
        templateUrl: 'appParts/loginComponent/login.html',
        controller : 'loginController'
    })
    .state('klijent', {
        url: '/klijent',
        templateUrl: 'appParts/klijentComponent/klijentHome.html',
        controller : 'klijentHomeController'
    })
    .state('racuni', {
        url: '/racuni',
        templateUrl: 'appParts/klijentIzvodiComponent/klijentIzvodi.html',
        controller : 'klijentIzvodiController'
    })
    .state('nalog', {
        url: '/nalog',
        templateUrl: 'appParts/nalogComponent/nalog.html',
        controller : 'nalogController'
    })
    .state('noviRacun', {
        url: '/noviRacun',
        templateUrl: 'appParts/kreirajRacunComponent/kreirajRacun.html',
        controller : 'kreirajRacunController'
    })
    .state('upravljanjeRacunima', {
        url: '/upravljanjeRacunima',
        templateUrl : 'appParts/upravljanjeRacunimaComponent/upravljanjeRacunima.html',
        controller : 'upravljanjeRacunimaController'
    })    
    .state('izvestaji', {
        url: '/izvestaji',
        templateUrl : 'appParts/izvestajiComponent/izvestaji.html',
        controller : 'izvestajiController'
    })
    .state('importNaloga', {
        url: '/importNaloga',
        templateUrl : 'appParts/importComponent/importComponent.html',
        controller : 'importController'
    })

});