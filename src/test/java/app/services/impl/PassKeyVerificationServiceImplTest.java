package app.services.impl;

import app.services.api.PassKeyVerificationService;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PassKeyVerificationServiceImplTest{

    private static final String NON_HAHSED_PASS = "1111111111";
    private static final String SECOND_NON_HAHSED_PASS = "2222222222";
    private static final int WORKLOAD = 15;
    private static final String BCRYPT_START = "$2a$";
    private static final String CORRECT_HASHED_PASS = "$2a$10$bjNBEn8NGtyUdVGW060bLeQ27TeRfWB.j6bEVVL6b9vYQbZrSE2G.";
    private static final String INCORRECT_HASHED_PASS = "$2r$12$bjNBEnsNGtyUdVGW060bLeQ27TeRfWB.j6bEVVL6b9vYQbZrSE2G.";
    private static final String SYSTEM_DIR = "user.dir";
    private static final String CORRECT_PASSKEY_RULES_DIR_NAME = "passkey_rules";
    private static final String INCORRECT_DIR_NAME = "barabara";
    private static final String CORRECT_PASSKEY_RULES_DIR_PATH = "\\src\\main\\java\\app\\services\\password_service\\passkey_rules";

    @Autowired
    private PassKeyVerificationService passKeyVerificationService;

    @Before
    public void init() throws Exception {
    }


    @Test
    public void validatePassKeyWithCorrectInput() throws Exception {

        Assert.assertEquals("PassKey validation not working correctly", "",this.passKeyVerificationService.validatePassKey(NON_HAHSED_PASS));

    }

    @Test
    public void validatePassKeyToGetCorrectRuleDirectoryPath() throws Exception {

        Field rulesDirNameField = this.passKeyVerificationService.getClass().getDeclaredField("RULES_DIR_NAME");
        rulesDirNameField.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        modifiersField.setInt(rulesDirNameField, rulesDirNameField.getModifiers() & ~Modifier.FINAL);
        rulesDirNameField.set(this.passKeyVerificationService, CORRECT_PASSKEY_RULES_DIR_NAME);

        String systemDir = System.getProperty(SYSTEM_DIR);
        File root = new File(systemDir);
        Method getRulePath = this.passKeyVerificationService.getClass().getDeclaredMethod("getRulesDirectory", File.class);
        getRulePath.setAccessible(true);
        String result = (String) getRulePath.invoke(this.passKeyVerificationService, root);
        String expected = systemDir + CORRECT_PASSKEY_RULES_DIR_PATH;

        Assert.assertEquals("PassKey do not get correct path", expected,result);

    }


    @Test
    public void testHashPassKeyToReturnCorrectHashType() throws Exception {

        String hashResult = this.passKeyVerificationService.hashPassKey(NON_HAHSED_PASS);
        Assert.assertTrue("HashPassKey do not return correct type",hashResult.startsWith(BCRYPT_START));
    }

    @Test
    public void testHashPassKeyToReturnCorrectWorkLoad() throws Exception {

        Field workloadField = this.passKeyVerificationService.getClass().getDeclaredField("workload");
        workloadField.setAccessible(true);
        workloadField.set(this.passKeyVerificationService, WORKLOAD);

        String hashResult = this.passKeyVerificationService.hashPassKey(NON_HAHSED_PASS);
        int result = Integer.parseInt(hashResult.replace(BCRYPT_START, "").substring(0,2));
        Assert.assertEquals("HashPassKey do not return correct value",WORKLOAD,result);
    }

    @Test(expected = RuntimeException.class)
    public void checkPasskeyWithNull() throws Exception {

        boolean hashResult = this.passKeyVerificationService.checkPassKey(NON_HAHSED_PASS, null);
    }

    @Test(expected = RuntimeException.class)
    public void checkPasskeyWithIncorrectHash() throws Exception {

        boolean hashResult = this.passKeyVerificationService.checkPassKey(NON_HAHSED_PASS, INCORRECT_HASHED_PASS);
    }

    @Test
    public void checkPasskeyWithCorrectHash() throws Exception {

        boolean hashResult = this.passKeyVerificationService.checkPassKey(NON_HAHSED_PASS, CORRECT_HASHED_PASS);
        Assert.assertTrue("Passkey checking is not working properly", hashResult);
    }

    @Test
    public void checkPasskeyWithInCorrectPasskey() throws Exception {

        boolean hashResult = this.passKeyVerificationService.checkPassKey(SECOND_NON_HAHSED_PASS, CORRECT_HASHED_PASS);
        Assert.assertFalse("Passkey checking is not working properly", hashResult);
    }

}