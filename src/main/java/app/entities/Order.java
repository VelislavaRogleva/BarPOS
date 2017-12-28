package app.entities;

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
    @Check(constraints = "status = 'OPEN'" +
            "OR status = 'CLOSED'," +
            "OR status = 'CANCELED'")
    @Basic(optional = false)
    private Enum status;


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

    public Enum getStatus() {
        return status;
    }

    public void setStatus(Enum status) {
        this.status = status;
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
