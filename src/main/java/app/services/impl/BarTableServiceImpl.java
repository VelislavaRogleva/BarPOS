package app.services.impl;

import app.entities.BarTable;
import app.repositories.BarTableRepository;
import app.services.api.BarTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BarTableServiceImpl implements BarTableService {

    private final BarTableRepository barTableRepository;

    @Autowired
    public BarTableServiceImpl(BarTableRepository barTableRepository) {
        this.barTableRepository = barTableRepository;
    }

    @Override
    public BarTable findTableById(Long id) {
        return this.barTableRepository.getOne(id);
    }

    @Override
    public List<BarTable> getAllBarTables() {
        return this.barTableRepository.findAll();
    }

    @Override
    public void save(BarTable barTable) {
        this.barTableRepository.save(barTable);
    }
}
