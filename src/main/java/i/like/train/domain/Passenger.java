package i.like.train.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "passenger")
public class Passenger implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long version = 1L;

    public Passenger() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "version=" + version +
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
