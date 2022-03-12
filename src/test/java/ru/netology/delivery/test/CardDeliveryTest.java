package ru.netology.delivery.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {
    DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
    DataGenerator.UserInfo validUserEn = DataGenerator.Registration.generateUser("en");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Step(value = "Открытие страницы, очистка поля даты")
    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы на пустое значение поля: город")
    @Test
    void shouldOrderCardWithEmptyFieldByCity() {
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(3));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы на пустое значение поля: Имя")
    @Test
    void shouldOrderCardWithEmptyFieldByName() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы на пустое значение поля: Телефон")
    @Test
    void shouldOrderCardWithEmptyFieldByPhone() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(3));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Попытка заказа карты без отметки об обработке данных")
    @Test
    void shouldOrderCardWithoutCheckBox() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(3));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $(byText("Запланировать")).click();
        $(".checkbox.input_invalid").shouldBe(visible);
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Попытка заказать карту на дату менее чем через 3 дня от текущей даты")
    @Test
    void shouldOrderCardWithDateLessThreeDays() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(2));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=date] .input_invalid .input__sub")
                .shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы на двойное имя с дефисом")
    @Test
    public void shouldSendFormWithDoubleSurname() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        String dateDelivery = DataGenerator.generateDate(3);
        $("[data-test-id=date] .input__control").setValue(dateDelivery);
        $("[data-test-id=name] .input__control").setValue(DataGenerator.generateDoubleSurname());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(text("Встреча успешно запланирована на " + dateDelivery), Duration.ofSeconds(15));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы на двойное название города с дефисом")
    @Test
    public void shouldSendFormByNameOfCityWithDash() {
        $("[data-test-id=city] .input__control").setValue(DataGenerator.generateDoubleCity());
        String dateDelivery = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] .input__control").setValue(dateDelivery);
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(text("Встреча успешно запланирована на " + dateDelivery), Duration.ofSeconds(15));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы на некириллические значения по полю Имя")
    @Test
    public void shouldSendFormWithNotCyrillicSymbolsByName() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").
                setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        $("[data-test-id=name] .input__control").setValue(validUserEn.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(text("Имя и Фамилия указаные неверно."));
    }

    @Epic(value = "Услуги банка")
    @Feature(value= "Функционал для заказа карты с доставкой")
    @Story("Проверка формы, если указанным город не относится к списку доступных городов")
    @Test
    public void shouldSendFormWithCityNotInList() {
        $("[data-test-id=city] .input__control").setValue(DataGenerator.generateLocalCity());
        $("[data-test-id=date] .input__control")
                .setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(text("Доставка в выбранный город недоступна"));
    }
}
