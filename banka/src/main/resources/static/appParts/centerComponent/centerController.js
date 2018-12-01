mainModule.controller('centerController', ['$scope', '$window', 'mainService',
    function($scope, $window, mainService){

        $scope.cardNumber = "";
        $scope.format = function(){
            document.getElementById('cardNumber').addEventListener('input', function (e) {
            var target = e.target, position = target.selectionEnd, length = target.value.length;

            target.value = target.value.replace(/[^\dA-Z]/g, '').replace(/(.{4})/g, '$1 ').trim();
            target.selectionEnd = position += ((target.value.charAt(position - 1) === ' ' && target.value.charAt(length - 1) === ' ' && length !== target.value.length) ? 1 : 0);
            });
        }

        $scope.submit = function(){
            var poruka = mainService.validate();
            if(poruka=="yeet")
                document.getElementById("poruka").innerHTML  = "";
            else document.getElementById("poruka").innerHTML  = poruka;

        }



    }
]);