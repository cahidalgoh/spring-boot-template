package es.nextdigital.demo.service;

import es.nextdigital.demo.model.Transaction;

import java.math.BigDecimal;

public interface WithdrawService {

    Transaction withdraw(String cardNumber, Long atmId, BigDecimal amount);

}
