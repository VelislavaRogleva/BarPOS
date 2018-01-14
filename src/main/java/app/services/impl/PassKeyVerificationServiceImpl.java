package app.services.impl;

import app.enums.ErrorMessages;
import app.services.api.StatisticService;
import app.services.api.PassKeyVerificationService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class PassKeyVerificationServiceImpl implements PassKeyVerificationService {

    private static final String SYSTEM_DIR = "user.dir";
    private static final String RULES_DIR_NAME = "rules";
    private static final String HASHED_PASSKEY_START = "$2a$";
    private static final String JAVA_EXTENSION = ".java";
    private static final String START_PACKAGE_NAME = "app";


    //workload for BCrypt between 10 and 31 - determines the length of the salt
    private int workload = 10;

    //rules for password validation
    private List<StatisticService.PassKeyRule> rules;

    public PassKeyVerificationServiceImpl()  {
        this.rules = new ArrayList<>();
    }

    @Override
    public String validatePassKey(String passkey) {
        String validateError="";
        try {
            this.addRules();
            for (StatisticService.PassKeyRule rule : this.rules) {
                rule.checkPassKey(passkey);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | IOException | ClassNotFoundException e) {
            validateError = ErrorMessages.SYSTEM_ERROR.toString();
        } catch (IllegalArgumentException e) {
           validateError= e.getMessage();

        }
        return validateError;
    }

    @Override
    public String hashPassKey(String plainTextPasskey){
        String salt = BCrypt.gensalt(this.workload);
        return BCrypt.hashpw(plainTextPasskey, salt);
    }

    @Override
    public boolean checkPassKey(String plainTextPasskey, String storedHash) throws RuntimeException {

        if (null == storedHash || !storedHash.startsWith(HASHED_PASSKEY_START)){
            throw new RuntimeException(ErrorMessages.INVALID_DB_HASH.toString());
        }

        return BCrypt.checkpw(plainTextPasskey, storedHash);
    }

    private String getRulesDirectory(File root){
        if (root.getName().equalsIgnoreCase(RULES_DIR_NAME)){
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
            throw new IllegalArgumentException(ErrorMessages.PASSKEY_RULES_NOT_EXISTENT.toString());
        }
        File directory = new File(workDir);

        return directory.listFiles();
    }

    private void addRules() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        File[] ruleFiles = this.getRuleFilesFromPackage();
        if (null == ruleFiles){
            throw new IllegalArgumentException(ErrorMessages.PASSKEY_RULES_NOT_FOUND.toString());
        }
        for (File file : ruleFiles) {
            String absolutePath = file.getAbsolutePath();
            String dotSeparatedPath = absolutePath.substring(absolutePath.indexOf(START_PACKAGE_NAME)).replace("\\", ".").replace(JAVA_EXTENSION, "");
            Class<StatisticService.PassKeyRule> classFile = (Class<StatisticService.PassKeyRule>) Class.forName(dotSeparatedPath);
            Constructor<StatisticService.PassKeyRule> ruleConstructor = classFile.getDeclaredConstructor();
            this.rules.add(ruleConstructor.newInstance());
        }
    }
}



