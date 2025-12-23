package es.nextdigital.demo.service;

import es.nextdigital.demo.model.Transaction;

import java.math.BigDecimal;

public interface TransferService {
    Transaction transfer(String cardNumber, String destinationIban, BigDecimal amount);
}
