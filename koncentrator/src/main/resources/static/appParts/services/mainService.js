(function() { "use strict";

    mainModule.service('mainService', [ '$http', '$window','$localStorage', 
        function($http, $window, $localStorage) {

            this.dobaviTipovePlacanja = function(token){

                var req = {
                    method: 'GET',
                    url: ROOT_PATH+'getSupportedPaymentTypes/AAA'+token,
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
        }
    ]);
})()
