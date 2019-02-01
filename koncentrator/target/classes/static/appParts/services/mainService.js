(function() { "use strict";

    mainModule.service('mainService', [ '$http', '$window','$localStorage', 
        function($http, $window, $localStorage) {

            this.dobaviTipovePlacanja = function(token){

                var req = {
                    method: 'GET',
                    url: ROOT_PATH+'getSupportedPaymentTypes/'+token,
                }
                return $http(req);
            }

            this.preusmeriNaBanku = function(token){

                var req = {
                    method: 'GET',
                    url: ROOT_PATH+'sendRedirectToBanka/AAA'+token,
                }
                return $http(req);
            }

            this.obaviPlacanje = function(tipPlacanjaId, token){

                var req = {
                    method: 'POST',
                    url: ROOT_PATH+'doPayment/',
                    params: {
                        paymentTypeId : tipPlacanjaId,
                        uniqueToken : token
                    }
                }
                return $http(req);
            }
            this.proveriStatus = function(token){

                var req = {
                    method: 'GET',
                    url: ROOT_PATH+'proveriStatusTransakcije/',
                    params: {
                        uniqueToken : token
                    }
                }
                return $http(req);
            }
            this.obaviVracanje = function(token){

                var req = {
                    method: 'GET',
                    url: ROOT_PATH+'obaviVracanje/',
                    params: {
                        uniqueToken : token
                    }
                }
                return $http(req);
            }
        }
    ]);
})()
