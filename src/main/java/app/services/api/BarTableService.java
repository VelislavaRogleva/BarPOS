package app.services.api;

import app.entities.BarTable;

public interface BarTableService {
    BarTable findTableById(Long id);
}
