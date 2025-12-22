package es.nextdigital.demo.mapper;

import es.nextdigital.demo.dto.AccountDTO;
import es.nextdigital.demo.dto.TransactionDTO;
import es.nextdigital.demo.model.Account;
import es.nextdigital.demo.model.Transaction;

public class TransactionMapper {

    public static TransactionDTO toDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setType(tx.getType().name());
        dto.setAmount(tx.getAmount());
        dto.setTimestamp(tx.getTimestamp());
        dto.setAccount(toDTO(tx.getAccount()));
        return dto;
    }

    private static AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setIban(account.getIban());
        dto.setBalance(account.getBalance());
        return dto;
    }
}
