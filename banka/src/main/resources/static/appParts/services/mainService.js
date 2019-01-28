mainModule.service('mainService', [ '$http','jwtHelper','$window','$localStorage',
    function($http, jwtHelper, $window, $localStorage) {
        $window.onpopstate = function (e) { window.history.forward(1); }
        this.validate = function(){

            var cardNumber = document.getElementById("cardNumber").value.replace(/ /g,'');
            var cvv = document.getElementById("cvv").value;
            var owner = document.getElementById("owner").value;
            var ownerL = document.getElementById("ownerL").value;
            var month = document.getElementById("mesec");
            var selectedMonth = month.options[month.selectedIndex].text;
            var year = document.getElementById("godina");
            var selectedYear = year.options[year.selectedIndex].text;

            /*alert(
                "owner:" + owner +"\n"+
                "cvv:" + cvv +"\n"+
                "cardNo:" + cardNumber +"\n"+
                "selectedMonth:" + selectedMonth +"\n"+
                "selectedYear:" + selectedYear
            )*/
            if(!(/^\d+$/.test(cardNumber)))
                return "Broj kreditne kartice nije ispravan.";

            if(!(/^\d+$/.test(cvv)))
                return "CVV kod nije ispravan.";

            if(cardNumber==null || cardNumber=="")
                return "Polje za broj kartice mora biti popunjeno.";

            if(owner==null || owner=="")
                return "Ime mora biti popunjeno.";

            if(ownerL==null || ownerL=="")
                return "Prezime mora biti popunjeno.";

            if(cvv==null || cvv=="")
                return "Polje CVV kod mora biti popunjeno.";

            if(cvv<0)
                return "CVV kod nije ispravan.";

            if(cardNumber<0)
                return "Broj kartice kod nije ispravan.";

            if(cardNumber.toString().length<8)
                return "Broj kartice je previše kratak.";

            if(cvv.toString().length<3)
                return "CVV kod nije ispravan. Kod je previše kratak.";

            if(/[^a-z]/i.test(owner))
                return "Ime ne sme sadržati brojeve ili specijalne karaktere."

            if(/[^a-z]/i.test(ownerL))
                return "Prezime ne sme sadržati brojeve ili specijalne karaktere."


            return "yeet";
        }

        this.ulogujSe = function(user){
            var req = {
                method: 'POST',
                url: 'http://localhost:8096/rest/login',
                data: user
            }
            return $http(req);
        }


    }

]);