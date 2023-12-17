package ru.netology.aqa.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.aqa.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PaymentPage {
    private final SelenideElement heading = $x("/html/body/div[1]/div/h3");
    private final ElementsCollection inputFields = $$("input");
    private final ElementsCollection errorMessages = $$(".input__sub");
    private final SelenideElement notificationApproved = $("div.notification_status_ok");
    private final SelenideElement notificationDeclined = $("div.notification_status_error");
    private final ElementsCollection visibleNotifications = $$(".notification").filterBy(visible);
    private final SelenideElement cardNumberField = inputFields.get(0);
    private final SelenideElement cardExpireMonthField = inputFields.get(1);
    private final SelenideElement cardExpireYearField = inputFields.get(2);
    private final SelenideElement cardOwnerNameField = inputFields.get(3);
    private final SelenideElement cardCVCField = inputFields.get(4);
    private final SelenideElement processButton = $(byText("Продолжить"));

    public PaymentPage() {
        heading.shouldHave(exactText("Оплата по карте")).shouldBe(visible);
    }

    public void shouldBeError(String message) {
//        errorMessages.shouldHave(texts(message));
        $(byText(message)).shouldBe(visible);
    }

    public void shouldBeNoErrors() {
        errorMessages.shouldBe(empty);
    }

    public void shouldBeApprovedMessage() {
        notificationApproved.shouldBe(visible);
    }

    public void shouldBeDeclinedMessage() {
        notificationDeclined.shouldBe(visible);
    }

    public void processTheCard(DataHelper.CardInfo cardInfo) {
        cardNumberField.setValue(cardInfo.getCardNumber());
        cardExpireMonthField.setValue(cardInfo.getCardExpireMonth());
        cardExpireYearField.setValue(cardInfo.getCardExpireYear());
        cardOwnerNameField.setValue(cardInfo.getCardOwnerName());
        cardCVCField.setValue(cardInfo.getCardCVC());
        processButton.click();
    }

    public void processTheCardAndWait(DataHelper.CardInfo cardInfo) {
        processTheCard(cardInfo);
        //processButton.shouldNotBe(text("Отправляем запрос в Банк"), Duration.ofSeconds(DataHelper.secondsToWait));
        visibleNotifications.shouldHave(sizeGreaterThan(0), Duration.ofSeconds(DataHelper.secondsToWait));
    }
}
