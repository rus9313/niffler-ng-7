package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class RegisterTest {
    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        String username = RandomDataUtils.randomUsername();
        String password = "12345";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .signIn()
                .login(username, password)
                .checkFieldsAtPage();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String username = RandomDataUtils.randomUsername();
        String password = "12345";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .signIn()
                .createNewAccount()
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .checkMessageError();
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = RandomDataUtils.randomUsername();
        String password = "12345";
        String otherPassword = "abcd";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(otherPassword)
                .signUp()
                .checkMessageError();
    }

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String username = RandomDataUtils.randomUsername();
        String password = "12345";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .signIn()
                .login(username, password)
                .checkFieldsAtPage();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = "!!!";
        String password = "12345";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(username, password);
        new LoginPage().checkMessageError();
    }
}
