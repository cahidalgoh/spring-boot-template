package es.nextdigital.demo.controller;

import es.nextdigital.demo.model.ATM;
import es.nextdigital.demo.model.Account;
import es.nextdigital.demo.model.Card;
import es.nextdigital.demo.model.CardType;
import es.nextdigital.demo.repository.ATMRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
// Registra y prepara el bean MockMvc en el contexto de pruebas.
@AutoConfigureMockMvc// Configura automáticamente un objeto MockMvc dentro del contexto de pruebas.
@ActiveProfiles("test")// Le indica a Spring Boot que cargue application-test.properties en lugar de application.properties.
@Transactional
public class ATMControllerIntegrationTest {

    // Utilidad de Spring para simular llamadas HTTP a los controladores sin necesidad de levantar un servidor real
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ATMRepository atmRepository;

    private Account account;
    private Card debitCard;
    private ATM atm;

    @BeforeEach
    void setUp() {
        /*accountRepository.deleteAll();
        cardRepository.deleteAll();
        atmRepository.deleteAll();*/
        // Crear cuenta
        /*account = new Account();
        account.setIban("ES1234567890");
        account.setBalance(BigDecimal.valueOf(1000));
        accountRepository.save(account);

        // Crear tarjeta débito activa
        debitCard = new Card();
        debitCard.setCardNumber("1111-2222-3333-4444");
        debitCard.setType(CardType.DEBIT);
        debitCard.setActive(true);
        debitCard.setWithdrawLimit(BigDecimal.valueOf(6000));
        debitCard.setAccount(account);
        debitCard.setBankId("BANK1");
        cardRepository.save(debitCard);

        // Crear cajero del mismo banco
        atm = new ATM();
        atm.setBankId("BANK1");
        atm.setLocation("Sucursal Central");
        atm.setSupportsDeposit(true);
        atmRepository.save(atm);*/

        accountRepository.deleteAll();
        cardRepository.deleteAll();
        atmRepository.deleteAll();

        // Crear cuenta
        account = new Account();
        account.setIban("ES1234567890");
        account.setBalance(BigDecimal.valueOf(1000));
        accountRepository.save(account);

        // Crear tarjeta débito activa
        debitCard = new Card();
        debitCard.setCardNumber("1111-2222-3333-4444");
        debitCard.setType(CardType.DEBIT);
        debitCard.setActive(true);
        debitCard.setWithdrawLimit(BigDecimal.valueOf(6000));
        debitCard.setAccount(account);
        debitCard.setBankId("BANK1");
        // inicializa creditAvailable aunque sea débito, para evitar nulls
        debitCard.setCreditAvailable(BigDecimal.valueOf(0));
        cardRepository.save(debitCard);

        // Crear cajero del mismo banco
        atm = new ATM();
        atm.setBankId("BANK1");
        atm.setLocation("Sucursal Central");
        atm.setSupportsDeposit(true);
        atmRepository.save(atm);
    }

    @Test
    void testWithdrawSuccess() throws Exception {

        String json = """
        {
            "cardNumber": "1111-2222-3333-4444",
            "amount": 100
        }
        """;

        mockMvc.perform(post("/atm/" + atm.getId() + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(100));
        /*
        mockMvc.perform(post("/atm/" + atm.getId() + "/withdraw")
                        .param("cardNumber", debitCard.getCardNumber())
                        .param("amount", "100"))
                        .andDo(print())   // <-- imprime la respuesta completa
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(100));

         */
    }

    @Test
    void testWithdrawInsufficientBalance() throws Exception {
        String json = """
                {
                    "cardNumber": "1111-2222-3333-4444",
                    "amount": 2000
                }
                """;
        mockMvc.perform(post("/atm/" + atm.getId() + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDepositSuccess() throws Exception {
        String body = """
        {"cardNumber":"1111-2222-3333-4444","amount":200}
        """;

        mockMvc.perform(post("/atm/" + atm.getId() + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(200));
    }

    @Test
    void testDepositOtherBankATM() throws Exception {
        ATM otherAtm = new ATM();
        otherAtm.setBankId("OTHERBANK");
        otherAtm.setLocation("Sucursal externa");
        otherAtm.setSupportsDeposit(true);
        atmRepository.save(otherAtm);

        String body = """
        {"cardNumber":"1111-2222-3333-4444","amount":200}
        """;

        mockMvc.perform(post("/atm/" + otherAtm.getId() + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
