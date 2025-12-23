package es.nextdigital.demo.controller;

import es.nextdigital.demo.model.Account;
import es.nextdigital.demo.model.Card;
import es.nextdigital.demo.model.CardType;
import es.nextdigital.demo.repository.AccountRepository;
import es.nextdigital.demo.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ATMControllerTransferIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    private Account origin;
    private Account destination;
    private Card debitCard;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        cardRepository.deleteAll();

        origin = new Account();
        origin.setIban("ES1111111111111111111111");
        origin.setBalance(BigDecimal.valueOf(1000));
        origin.setBankId("BANK1");
        accountRepository.save(origin);

        destination = new Account();
        destination.setIban("ES2222222222222222222222");
        destination.setBalance(BigDecimal.valueOf(500));
        destination.setBankId("BANK1"); // El mismo banco
        accountRepository.save(destination);

        debitCard = new Card();
        debitCard.setCardNumber("9999-8888-7777-6666");
        debitCard.setType(CardType.DEBIT);
        debitCard.setActive(true);
        debitCard.setWithdrawLimit(BigDecimal.valueOf(5000));
        debitCard.setAccount(origin);
        debitCard.setBankId("BANK1");
        debitCard.setCreditAvailable(BigDecimal.ZERO);
        cardRepository.save(debitCard);
    }

    @Test
    void testTransferSuccess() throws Exception {
        String body = """
        {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES2222222222222222222222","amount":200}
        """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSFER_OUT"))
                .andExpect(jsonPath("$.amount").value(200));
    }

    @Test
    void testTransferInsufficientBalance() throws Exception {
        String body = """
        {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES2222222222222222222222","amount":2000}
        """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTransferDestinationNotFound() throws Exception {
        String body = """
        {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES9999999999999999999999","amount":100}
        """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTransferSameAccount() throws Exception {
        String body = """
        {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES1111111111111111111111","amount":100}
        """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTransferInactiveCard() throws Exception {
        debitCard.setActive(false);
        cardRepository.save(debitCard);

        String body = """
    {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES2222222222222222222222","amount":100}
    """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTransferCreditCardSuccess() throws Exception {
        Card creditCard = new Card();
        creditCard.setCardNumber("5555-4444-3333-2222");
        creditCard.setType(CardType.CREDIT);
        creditCard.setActive(true);
        creditCard.setAccount(origin);
        creditCard.setBankId("BANK1");
        creditCard.setCreditAvailable(BigDecimal.valueOf(1000));
        cardRepository.save(creditCard);

        String body = """
    {"cardNumber":"5555-4444-3333-2222","destinationIban":"ES2222222222222222222222","amount":300}
    """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSFER_OUT"))
                .andExpect(jsonPath("$.amount").value(300));
    }

    @Test
    void testTransferCreditCardLimitExceeded() throws Exception {
        Card creditCard = new Card();
        creditCard.setCardNumber("5555-4444-3333-2222");
        creditCard.setType(CardType.CREDIT);
        creditCard.setActive(true);
        creditCard.setAccount(origin);
        creditCard.setBankId("BANK1");
        creditCard.setCreditAvailable(BigDecimal.valueOf(200));
        cardRepository.save(creditCard);

        String body = """
    {"cardNumber":"5555-4444-3333-2222","destinationIban":"ES2222222222222222222222","amount":500}
    """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTransferUpdatesBalances() throws Exception {
        String body = """
    {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES2222222222222222222222","amount":200}
    """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk());

        Account updatedOrigin = accountRepository.findByIban("ES1111111111111111111111");
        Account updatedDestination = accountRepository.findByIban("ES2222222222222222222222");

        // saldo origen: 1000 - 200 = 800
        // saldo destino: 500 + 200 = 700
        assertEquals(BigDecimal.valueOf(800), updatedOrigin.getBalance());
        assertEquals(BigDecimal.valueOf(700), updatedDestination.getBalance());
    }

    @Test
    void testTransferInvalidAmount() throws Exception {
        String body = """
    {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES2222222222222222222222","amount":0}
    """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTransferWithCommissionDifferentBank() throws Exception {
        // Cambiamos el banco de la cuenta destino
        destination.setBankId("BANK2");
        accountRepository.save(destination);

        String body = """
    {"cardNumber":"9999-8888-7777-6666","destinationIban":"ES2222222222222222222222","amount":100}
    """;

        mockMvc.perform(post("/atm/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSFER_OUT"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.commission").value(1.0)); // 1% de 100 = 1

        Account updatedOrigin = accountRepository.findByIban("ES1111111111111111111111");
        Account updatedDestination = accountRepository.findByIban("ES2222222222222222222222");

        // saldo origen: 1000 - (100 + 1) = 899
        // saldo destino: 500 + 100 = 600
        assertEquals(0, updatedOrigin.getBalance().compareTo(BigDecimal.valueOf(899)));
        assertEquals(0, updatedDestination.getBalance().compareTo(BigDecimal.valueOf(600)));
    }
}
