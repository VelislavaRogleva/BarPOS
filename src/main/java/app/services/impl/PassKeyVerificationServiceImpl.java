package app.services.impl;

import app.services.password_service.PassKeyRule;
import app.services.api.PassKeyVerificationService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class PassKeyVerificationServiceImpl implements PassKeyVerificationService {

    private static final String RULES_PATH = "passkey_rules";
    private static final String SYSTEM_ERROR = "System Error";
    private static final String SYSTEM_DIR = "user.dir";
    private static final String PASSKEY_RULES_NOT_EXISTENT = "passkey_rules directory is renamed or is not existent";
    public static final String PASSKEY_RULES_NOT_FOUND = "Passkey rules not found";

    //workload for BCrypt between 10 and 31 - determines the length of the salt
    private int workload = 10;
    private String path;

    //rules for password validation
    private List<PassKeyRule> rules;

    public PassKeyVerificationServiceImpl()  {
        this.rules = new ArrayList<>();
    }

    @Override
    public String validatePassKey(String passkey) {
        String validateError="";
        try {
            this.addRules();
            for (PassKeyRule rule : this.rules) {
                rule.checkPassKey(passkey);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | IOException | ClassNotFoundException e) {
            validateError = SYSTEM_ERROR;
        } catch (IllegalArgumentException e) {
           validateError= e.getMessage();

        }
        return validateError;
    }

    @Override
    public String hashPassKey(String plainTextPasskey){
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(plainTextPasskey, salt);
    }

    @Override
    public boolean checkPassKey(String plainTextPasskey, String storedHash){
        boolean isVerified = false;

        if (null == storedHash || !storedHash.startsWith("$2a$")){
            throw new IllegalArgumentException("The hash stored in DB is invalid");
        }
        isVerified = BCrypt.checkpw(plainTextPasskey, storedHash);

        return isVerified;
    }


    private String getRulesDirectory(File root){
        if (root.getName().equals(RULES_PATH)){
            return root.getAbsolutePath();
        }
        File[] files = root.listFiles();
        if(files != null){
            for (File file: files) {
                if (file.isDirectory()){
                    String resultDir = getRulesDirectory(file);
                    if (null != resultDir){
                        return resultDir;
                    }
                }
            }
        }
        return null;
    }

    private File[] getRuleFilesFromPackage(){

        File root = new File(System.getProperty(SYSTEM_DIR));
        String workDir = getRulesDirectory(root);
        if (null == workDir){
            throw new IllegalArgumentException(PASSKEY_RULES_NOT_EXISTENT);
        }
        File directory = new File(workDir);

        return directory.listFiles();
    }

    private void addRules() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        File[] ruleFiles = this.getRuleFilesFromPackage();
        if (null == ruleFiles){
            throw new IllegalArgumentException(PASSKEY_RULES_NOT_FOUND);
        }
        for (File file : ruleFiles) {
            String absolutePath = file.getAbsolutePath();
            String dotSeparatedPath = absolutePath.substring(absolutePath.indexOf("app")).replace("\\", ".").replace(".java", "");
            Class<PassKeyRule> classFile = (Class<PassKeyRule>) Class.forName(dotSeparatedPath);
            Constructor<PassKeyRule> ruleConstructor = classFile.getDeclaredConstructor();
            this.rules.add(ruleConstructor.newInstance());
        }
    }
}


