package i.like.train.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Train implements Serializable {

    /**
     * Object attributes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long version = 2L;

    private Integer maxNumberOfPassenger;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Passenger> passengerList = new ArrayList<>();

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

    public Integer getMaxNumberOfPassenger() {
        return maxNumberOfPassenger;
    }

    public void setMaxNumberOfPassenger(Integer numberOfPassenger) {
        this.maxNumberOfPassenger = numberOfPassenger;
    }


    public Train numberOfPassenger(Integer defaultNumberOfPassenger) {
        this.maxNumberOfPassenger = defaultNumberOfPassenger;
        return this;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    @Override
    public String toString() {
        return "Train{" +
                "version=" + version +
                ", id=" + id +
                ", maxNumberOfPassenger=" + maxNumberOfPassenger +
                ", passengerList=" + passengerList +
                '}';
    }

}
