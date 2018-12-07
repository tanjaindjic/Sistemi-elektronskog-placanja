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

        }
    ]);
})()
