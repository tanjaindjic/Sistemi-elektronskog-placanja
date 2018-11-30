mainModule.service('userService', [ '$http','jwtHelper','$window','$localStorage',
    function($http, jwtHelper, $window, $localStorage) {

        this.ulogujSe = function(user){
            var req = {
                method: 'POST',
                url: 'http://localhost:8096/rest/login',
                data: user
            }
            return $http(req);
        }

        this.parsirajToken = function(){
            if($window.localStorage.getItem('token') == null){
                return {};
            }
            
            var tokenData = jwtHelper.decodeToken($window.localStorage.getItem('token'));
            return {id: tokenData.id, korisnickoIme : tokenData.sub, uloga : tokenData.uloga[0].authority}
        }

        this.proba = function(user){
            var req = {
                method: 'GET',
                url: 'http://localhost:8096/rest/proba',
                headers: {'token' : $window.localStorage.getItem('token')}
            }
            return $http(req);
        }


    }

]);