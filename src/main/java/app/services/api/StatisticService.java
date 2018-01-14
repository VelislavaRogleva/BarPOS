package app.services.api;

import app.dtos.StatisticProductDto;

import java.util.Date;
import java.util.List;

public interface StatisticService {
    /***
     * Returns StatisticProductDto basedd on start date and end date
     * And order status
     * @param startDate
     * @param endDate
     * @param orderStatus
     * @return
     */
    List<StatisticProductDto> getAllStatisticProducts(
            Date startDate,
            Date endDate,
            String orderStatus
    );

    interface PassKeyRule {

        boolean checkPassKey(String passkey);

    }
}
