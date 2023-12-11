package ru.netology.aqa.tests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.data.SQLHelper;
import ru.netology.aqa.pages.DashboardPage;
import ru.netology.aqa.pages.CreditRequestPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;

public class CreditTest {
    CreditRequestPage creditPage;
    static List<DataHelper.CardItem> cardItems;

    @BeforeAll
    @SneakyThrows
    static void setUpAll() {
        cardItems = DataHelper.getCardItemsFromFile(DataHelper.DataJSONLocation);
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanDatabase();
        var dashboardPage = open("http://localhost:8080", DashboardPage.class);
        creditPage = dashboardPage.chooseCredit();
    }

    @Test
    @DisplayName("Card №1 from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void shouldBeSuccess11() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());    //Дополняю номер карты остальными валидными данными
        creditPage.proceedTheCard(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            creditPage.shouldBeApprovedMessage();
        else
            creditPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("Card №1 from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void shouldBeSuccess12() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.proceedTheCard(cardInfo);
        var actualCreditRequestStatus = SQLHelper.getCreditRequestEntity().getStatus();
        var expectedCreditRequestStatus = cardItem.getCardStatus();
        Assertions.assertEquals(expectedCreditRequestStatus, actualCreditRequestStatus);
    }

    @Test
    @DisplayName("Card №2 from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void shouldBeSuccess21() {
        var cardItem = cardItems.get(1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.proceedTheCard(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            creditPage.shouldBeApprovedMessage();
        else
            creditPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("Card №2 from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void shouldBeSuccess22() {
        var cardItem = cardItems.get(1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.proceedTheCard(cardInfo);
        var actualCreditRequestStatus = SQLHelper.getCreditRequestEntity().getStatus();
        var expectedCreditRequestStatus = cardItem.getCardStatus();
        Assertions.assertEquals(expectedCreditRequestStatus, actualCreditRequestStatus);
    }

    @Test
    @DisplayName("Have the transaction_id's been saved to both tables")
    void shouldBeSuccess3() {
        creditPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var bank_idFromCreditRequestEntity = SQLHelper.getCreditRequestEntity().getBank_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertFalse(bank_idFromCreditRequestEntity.isEmpty());
        Assertions.assertFalse(transaction_idFromOrderEntity.isEmpty());
    }

    @Test
    @DisplayName("Are the transaction_id's the same in both tables")
    void shouldBeSuccess4() {
        creditPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var bank_idFromCreditEntity = SQLHelper.getCreditRequestEntity().getBank_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertEquals(transaction_idFromOrderEntity, bank_idFromCreditEntity);
    }

    @Test
    @DisplayName("Is order_entity.payment_id empty")
    void shouldBeSuccess5() {
        creditPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var credit_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertTrue(credit_idFromOrderEntity.isEmpty());
    }

    @Test
    @DisplayName("Is the payment_entity table empty")
    void shouldBeSuccess6() {
        creditPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var idFromPaymentRequestEntity = SQLHelper.getPaymentEntity().getId();
        Assertions.assertTrue(idFromPaymentRequestEntity.isEmpty());
    }
}
