package app.services.api;

import app.entities.BarTable;

import java.util.List;

public interface BarTableService {
    void addNewTable(BarTable barTable);

    BarTable findTableById(Long id);

    List<BarTable> getAllBarTables();

    void save(BarTable barTable);
}
