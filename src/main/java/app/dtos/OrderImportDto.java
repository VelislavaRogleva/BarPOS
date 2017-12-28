package app.dtos;

import javax.validation.constraints.NotNull;

public class OrderImportDto {

    @NotNull
    private Long tableId;
    @NotNull
    private String userName;

    public OrderImportDto(Long tableId, String userName) {
        this.tableId = tableId;
        this.userName = userName;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
