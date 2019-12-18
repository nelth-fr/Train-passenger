package i.like.train.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
public class Event implements Serializable {

    /**
     * Object Attributes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer version;

    private Timestamp createdAt;

    /**
     * Default constructor
     */
    public Event() {
        createdAt = new Timestamp(System.currentTimeMillis());
        version = 0;
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