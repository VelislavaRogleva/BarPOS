package app.entities;

import app.entities.enums.OrderStatus;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private Date date;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private OrderStatus status;


    @ManyToOne
    @JoinColumn(name = "table_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_table_id"))
    private BarTable barTable;

    @ManyToOne
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_user_id"))
    private User user;

    public Order() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = OrderStatus.valueOf(status.toUpperCase());
    }


    public BarTable getBarTable() {
        return barTable;
    }

    public void setBarTable(BarTable barTable) {
        this.barTable = barTable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}