package app.services.api;

import app.dtos.StatisticProductDto;

import java.util.Date;
import java.util.List;

public interface StatisticService {
    List<StatisticProductDto> getAllSoldProductsGroupedByOrderStatusOrderedByTotalAmountSold(
            Date startDate,
            Date endDate,
            String orderStatus
    );
}
