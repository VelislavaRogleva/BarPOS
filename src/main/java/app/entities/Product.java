package app.entities;


import app.dtos.StatisticProductDto;

import javax.persistence.*;

@Entity
@Table(name = "products")

@SqlResultSetMapping(
        name="productStatisticsMapping",
        classes={
                @ConstructorResult(
                        targetClass=StatisticProductDto.class,
                        columns={
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "cost"),
                                @ColumnResult(name = "price"),
                                @ColumnResult(name = "profit"),
                                @ColumnResult(name = "sold")
                        }
                )
        }
)

@NamedNativeQuery(name="Product.getProductQuantityStatistics",
        query="SELECT p.name, p.cost, p.price, (p.price - p.cost) AS profit, SUM(op.product_quantity) AS sold\n" +
                "FROM orders AS o\n" +
                "INNER JOIN order_products AS op\n" +
                "    ON op.order_id = o.id\n" +
                "INNER JOIN products p\n" +
                "    ON op.product_id = p.id\n" +
                "WHERE o.date >= :startDate\n" +
                "      AND o.date <= :endDate\n" +
                "      AND o.status LIKE :status\n" +
                "GROUP BY p.id\n" +
                "ORDER BY sold DESC", resultSetMapping="productStatisticsMapping")

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "barcode", unique = true)
    private String barcode;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "category_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_category_id"))
    private Category category;

    public Product () {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}