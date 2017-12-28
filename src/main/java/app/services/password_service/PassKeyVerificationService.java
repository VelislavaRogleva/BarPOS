package app.services.password_service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class PassKeyVerificationService implements PassKeyVerification {

    private static final String RULES_PATH = "passkey_rules";
    private static final String SRC_PATH ="src\\main\\java";
    private static final String PASSWORD_RULE_FILE_PATH = "app.services.password_service.passkey_rules.";
    private static final String SYSTEM_ERROR = "System Error";

    //workload for BCrypt between 10 and 31 - determines the length of the salt
    private  int workload = 10;

    //rules for password validation
    private List<PassKeyRule> rules;

    public PassKeyVerificationService()  {
        this.rules = new ArrayList<>();
    }

    @Override
    //public List<String> validatePassword(String password) {
    public String validatePassword(String password) {
        String validateError="";
        try {
            this.addRules();

            for (PassKeyRule rule : this.rules) {
                rule.checkPassword(password);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | IOException | ClassNotFoundException e) {
            validateError = SYSTEM_ERROR;
        } catch (IllegalArgumentException e) {
           validateError= e.getMessage();

        }
        return validateError;
    }

    @Override
    public String hashPassword(String plainTextPassword){
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(plainTextPassword, salt);
    }

    @Override
    public boolean checkPassword(String plainTextPassword, String storedHash){
        boolean isVerified = false;

        if (null == storedHash || !storedHash.startsWith("$2a$")){
            throw new IllegalArgumentException("The hash stored in DB is invalid");
        }
        isVerified = BCrypt.checkpw(plainTextPassword, storedHash);

        return isVerified;
    }

    private File[] getRuleFilesFromPackage(){
        String internalPath = this.getClass().getName().replace(".", File.separator);
        String externalPath = System.getProperty("user.dir") + File.separator + SRC_PATH;
        String workDir = externalPath + File.separator + internalPath.substring(0, internalPath.lastIndexOf(File.separator)) + File.separator + RULES_PATH;

        File directory = new File(workDir);

        return directory.listFiles();
    }

    private void addRules() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        File[] ruleFiles = this.getRuleFilesFromPackage();
        for (File file : ruleFiles) {

            Class<PassKeyRule> classFile = (Class<PassKeyRule>) Class.forName(PASSWORD_RULE_FILE_PATH + file.getName().replace(".java", ""));
            Constructor<PassKeyRule> ruleConstructor = classFile.getDeclaredConstructor();
            this.rules.add(ruleConstructor.newInstance());
        }
    }
}



