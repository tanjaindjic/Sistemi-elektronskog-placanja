(function() { "use strict";

	mainModule.controller('successController', [ '$scope','$window','$localStorage','$location', '$stateParams','mainService','$timeout',
        function($scope, $window, $localStorage, $location, $stateParams, mainService, $timeout) {
            $scope.token = $stateParams.token;
            $scope.paymentIdx = -1;
            $scope.tipoviPlacanja = [];
            $scope.poruka_prvi_red = "";
            $scope.poruka_drugi_red = "";
            $scope.status = "";
            
            $scope.init = function(){
            	mainService.proveriStatus($scope.token).then(
                        function successCallback(response){
                        	$scope.status = response.data;
                            if(response.data=="U"){
                            	$scope.poruka_prvi_red = "Plaćanje je uspešno obavljeno";
                            }
                            else if(response.data=="C"){
                            	$scope.poruka_prvi_red = "Transakcija još nije";
                            	$scope.poruka_drugi_red = "obavljena"
                            }
                            else if(response.data=="N"){
                            	$scope.poruka_prvi_red = "Transakcija nije uspešno";
                            	$scope.poruka_drugi_red = "obavljena"
                            }
                        },
                        function errorCallback(response){
                        	$scope.poruka_prvi_red = "Greška prilikom provere ";
                        	$scope.poruka_drugi_red = "statusa transakcije";
                        }
                    );
            }
            $scope.countdown = 5;
            $scope.updateCounter = function() {
            	if($scope.status=='U'){           
	                $scope.countdown--;
	                $timeout(countdown, 1000);
	                if($scope.countdown==-1){
	                	mainService.obaviVracanje($scope.token).then(
	                            function successCallback(response){
	                                if(response.headers('Location')){
	                                    $window.location.href = response.headers('Location');
	                                }else{
	                                    alert("Redirekcija nije uspešno obavljena, refrešujte stranicu.");
	                                }
	                            });
	                }
            	}
            };
            $scope.updateCounter();
            

        }
    ]);
})();