(function() { "use strict";

    mainModule.service('mainService', [ '$http', '$window','$localStorage', 
        function($http, $window, $localStorage) {

            this.testiraj = function(idNaloga){

                var req = {
                    method: 'GET',
                    url: ROOT_PATH+'test',
                }
                return $http(req);
            }

        }
    ]);
})()
