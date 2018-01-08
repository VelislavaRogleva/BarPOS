package app.services.impl;

import app.dtos.StatisticProductDto;
import app.repositories.ProductRepository;
import app.services.api.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<StatisticProductDto> getAllStatisticProducts(Date startDate, Date endDate, String orderStatus) {
        List<StatisticProductDto> statisticProductDtos = new ArrayList<>();
        String startDateString = stringifyDate(startDate);
        String endDateString = stringifyDate(endDate);
        List<Object[]> fetchSqlResults = this.productRepository
                .findAllSoldProductsOrOrderByTotalSoldAmountDesc(
                    startDateString, endDateString, orderStatus
                );
        for (Object[] fetchSqlResult : fetchSqlResults) {
            StatisticProductDto statisticProductDto = mapSqlResultToStatisticDto(fetchSqlResult);
            statisticProductDtos.add(statisticProductDto);
        }

        return statisticProductDtos;
    }

    private StatisticProductDto mapSqlResultToStatisticDto(Object[] fetchSqlResult) {
        StatisticProductDto statisticProductDto = new StatisticProductDto();
        statisticProductDto.setName((String) fetchSqlResult[0]);
        statisticProductDto.setCost((Double) fetchSqlResult[1]);
        statisticProductDto.setPrice((Double) fetchSqlResult[2]);
        statisticProductDto.setProfit((Double) fetchSqlResult[3]);
        statisticProductDto.setSold((BigDecimal) fetchSqlResult[4]);
        return statisticProductDto;
    }

    private String stringifyDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = dateFormat.format(date);
        return stringDate;
    }
}
