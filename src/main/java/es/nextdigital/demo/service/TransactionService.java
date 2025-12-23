package es.nextdigital.demo.service;

import es.nextdigital.demo.model.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactionsByAccountIban(String iban);
}
