(function() { "use strict";

    mainModule.controller('mainPanelController', [ '$scope','$window','$localStorage','$location', '$stateParams','mainService',
        function($scope, $window, $localStorage, $location, $stateParams, mainService) {
            $scope.token = $stateParams.token;
            $scope.paymentIdx = -1;

            $scope.selectPayment = function(idx){
                if(!isNaN(idx)){
                    $scope.paymentIdx = idx;
                }else{
                    $scope.paymentIdx = -1;
                }
            }

            $scope.potvrdi = function(){
                
                mainService.testiraj().then( 
                    function(response){
                        alert(response.data)
                    },
                    function(error){
                        alert('Greska prilikom potvrde placanja.');
                    }
                );
            }            

        }
    ]);
})();