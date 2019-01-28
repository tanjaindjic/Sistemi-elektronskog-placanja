(function() { "use strict";

    success.controller('successController', [ '$scope','$window','$localStorage','$location', '$stateParams','mainService',
        function($scope, $window, $localStorage, $location, $stateParams, mainService) {
            $scope.token = $stateParams.token;
            $scope.paymentIdx = -1;
            $scope.tipoviPlacanja = [];

            $scope.init = function(){

        /*        mainService.dobaviTipovePlacanja($scope.token).then(
                    function successCallback(response){
                        $scope.tipoviPlacanja = response.data;
                    },
                    function errorCallback(response){
                        alert("Greska prilikom dobavljanja tipova placanja.");
                    }
                );*/
            }

            

        }
    ]);
})();