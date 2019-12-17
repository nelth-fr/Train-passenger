package i.like.train.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Train implements Serializable {

    /**
     * Object attributes
     */
    private Long version = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numberOfPassenger;

    /**
     * Default constructor
     */
    public Train() { }

    /**
     * Getters and setters
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Integer getNumberOfPassenger() {
        return numberOfPassenger;
    }

    public void setNumberOfPassenger(Integer numberOfPassenger) {
        this.numberOfPassenger = numberOfPassenger;
    }

    @Override
    public String toString() {
        return "Train{" +
                "id=" + id +
                ", version=" + version +
                ", numberOfPassenger=" + numberOfPassenger +
                '}';
    }

    public Train numberOfPassenger(Integer defaultNumberOfPassenger) {
        this.numberOfPassenger = defaultNumberOfPassenger;
        return this;
    }

}
