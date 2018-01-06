package app.services.api;

import app.entities.BarTable;

import java.util.List;

public interface BarTableService {
    BarTable findTableById(Long id);

    List<BarTable> getAllBarTables();

    void save(BarTable barTable);
}
