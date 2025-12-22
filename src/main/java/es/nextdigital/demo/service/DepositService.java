package es.nextdigital.demo.service;

import es.nextdigital.demo.model.Transaction;

import java.math.BigDecimal;

public interface DepositService {
    Transaction deposit(String cardNumber, Long atmId, BigDecimal amount);
}
