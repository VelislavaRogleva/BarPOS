package app.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

public class StatisticProductDto implements Serializable {
    private String name;
    private Double cost;
    private Double price;
    private Double profit;
    private BigDecimal sold;

    public StatisticProductDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public BigDecimal getSold() {
        return sold;
    }

    public void setSold(BigDecimal sold) {
        this.sold = sold;
    }
}
