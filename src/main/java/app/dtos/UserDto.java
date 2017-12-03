package app.dtos;

import javafx.beans.property.SimpleStringProperty;

public class UserDto {

    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleStringProperty code = new SimpleStringProperty("");

    public UserDto() {
        this("", "");
    }

    public UserDto(String name, String code) {
        this.setName(name);
        this.setCode(code);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCode() {
        return code.get();
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }
}
