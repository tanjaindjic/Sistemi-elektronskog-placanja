--insert into klijent (email, ime, merchantid, merchant_pass, prezime) values ('prvi@gmail.com', 'prvi', '1', 'pass1', 'prvic')
--insert into klijent (email, ime, merchantid, merchant_pass, prezime) values ('drugi@gmail.com', 'drugi', '2', 'pass2', 'drugic')
--insert into klijent (email, ime, merchantid, merchant_pass, prezime) values ('treci@gmail.com', 'treci', '3', 'pass3', 'trecic')
--insert into kartica (br_racuna, ccv, exp_date, pan, raspoloziva_sredstva, rezervisana_sredstva, vlasnik_id) values ('111111001', '111', '1/25', '1111112233334444', 10000.0, 0.0, 1)
--insert into kartica (br_racuna, ccv, exp_date, pan, raspoloziva_sredstva, rezervisana_sredstva, vlasnik_id) values ('111111002', '222', '1/25', '1111113344445555', 10000.0, 0.0, 2)
--insert into kartica (br_racuna, ccv, exp_date, pan, raspoloziva_sredstva, rezervisana_sredstva, vlasnik_id) values ('111111003', '333', '1/25', '1111114455556666', 10000.0, 0.0, 3)
--insert into klijent_kartice (klijent_id, kartice_id) values (1, 1),(2, 2),(3, 3)
--insert into transakcija (errorurl, failedurl, iznos, merchant_order_id, merchant_timestamp, pan_posaljioca, pan_primaoca, paymenturl, prima_id, status, successurl, timestamp, uplacuje_id) values ('error', 'failed', 100.0, 5, NOW() , null, '1111112233334444', '1', null, 'K', 'succ', NOW() , null)
--insert into payment_info (paymenturl, transakcija_orderid) values ('1', 1)
--insert into transakcija (errorurl, failedurl, iznos, merchant_order_id, merchant_timestamp, pan_posaljioca, pan_primaoca, paymenturl, prima_id, status, successurl, timestamp, uplacuje_id) values ('error', 'failed', 100.0, 5,  NOW() , null, '1111112233334444', '1', null, 'K', 'succ', NOW() , null)
--update transakcija set merchant_timestamp = DateAdd("mm",29,merchant_timestamp);
--update transakcija set timestamp = DateAdd("mm",29,timestamp);
--insert into payment_info (paymenturl, transakcija_orderid) values ('2', 2)
--IMAM PROBLEM SA TIMESTAMPOM, STAVLJA POGRESAN DATUM KONSTATNO