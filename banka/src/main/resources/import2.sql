insert into klijent (email, ime, merchantid, merchant_pass, prezime) values ('prvi@gmail.com', 'prvi', '1', 'pass1', 'prvic')
insert into klijent (email, ime, merchantid, merchant_pass, prezime) values ('drugi@gmail.com', 'drugi', '2', 'pass2', 'drugic')
insert into klijent (email, ime, merchantid, merchant_pass, prezime) values ('treci@gmail.com', 'treci', '3', 'pass3', 'trecic')
insert into kartica (br_racuna, ccv, exp_date, pan, raspoloziva_sredstva, rezervisana_sredstva, vlasnik_id) values ('222222001', '111', '1/25', '2222222233334444', 0.0, 0.0, 1)
insert into kartica (br_racuna, ccv, exp_date, pan, raspoloziva_sredstva, rezervisana_sredstva, vlasnik_id) values ('222222002', '222', '1/25', '2222222244445555', 10000.0, 0.0, 2)
insert into kartica (br_racuna, ccv, exp_date, pan, raspoloziva_sredstva, rezervisana_sredstva, vlasnik_id) values ('222222003', '333', '1/25', '2222222255556666', 10000.0, 0.0, 3)
insert into klijent_kartice (klijent_id, kartice_id) values (1, 1),(2, 2),(3, 3)
