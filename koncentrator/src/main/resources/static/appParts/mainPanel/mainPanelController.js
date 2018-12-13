(function() { "use strict";

    mainModule.controller('mainPanelController', [ '$scope','$window','$localStorage','$location', '$stateParams','mainService',
        function($scope, $window, $localStorage, $location, $stateParams, mainService) {
            $scope.token = $stateParams.token;
            $scope.paymentIdx = -1;
            $scope.tipoviPlacanja = [];

            $scope.init = function(){

                mainService.dobaviTipovePlacanja($scope.token).then(
                    function successCallback(response){
                        $scope.tipoviPlacanja = response.data;
                    },
                    function errorCallback(response){
                        alert("Greska prilikom dobavljanja tipova placanja.");
                    }
                );
            }

            $scope.selectPayment = function(idx){
                if(!isNaN(idx)){
                    $scope.paymentIdx = idx;
                }else{
                    $scope.paymentIdx = -1;
                }
            }

            $scope.potvrdi = function(){

                mainService.obaviPlacanje($scope.paymentIdx, $scope.token).then(
                    function successCallback(response){
                        if(response.headers('Location')){
                            $window.location.href = response.headers('Location');
                        }else{
                            alert("Placanje uspesno zabelezeno.");
                        }
                    },
                    function errorCallback(response){
                        alert("Greska prilikom obavljanja placanja.");
                    }
                );
            }

        }
    ]);
})();