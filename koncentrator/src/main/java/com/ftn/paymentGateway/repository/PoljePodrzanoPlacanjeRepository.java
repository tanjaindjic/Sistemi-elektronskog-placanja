package com.ftn.paymentGateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;

public interface PoljePodrzanoPlacanjeRepository extends JpaRepository<PoljePodrzanoPlacanje, Long>{


}
