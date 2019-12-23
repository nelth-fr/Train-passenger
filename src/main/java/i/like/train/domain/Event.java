package i.like.train.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "event")
public class Event implements Serializable {

    /**
     * Object Attributes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer version;

    @NotNull
    private Timestamp createdAt;

    /**
     * Default constructor
     */
    public Event() {
        version = 0;
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Event (Integer version) {
        this.version = version;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", version=" + version +
                ", createdAt=" + createdAt +
                '}';
    }

    @ManyToOne(optional = false)
    private Train trains;

    public Train getTrains() {
        return trains;
    }

    public void setTrains(Train trains) {
        this.trains = trains;
    }
}
