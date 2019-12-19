package i.like.train.domain;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "train")
public class Train implements Serializable {

    /**
     * Object attributes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long version = 3L;

    private Integer maxNumberOfPassenger;

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "trains" )
    private List<Passenger> passengerList = new ArrayList<>();

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "trains" )
    private List<Event> eventList = new ArrayList<>();

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

    // Setter used for testing purpose
    public Train maxNumberOfPassenger(Integer defaultNumberOfPassenger) {
        this.maxNumberOfPassenger = defaultNumberOfPassenger;
        return this;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public String toString() {
        return "Train{" +
                "id=" + id +
                ", version=" + version +
                ", maxNumberOfPassenger=" + maxNumberOfPassenger +
                ", passengerList=" + passengerList +
                ", eventList=" + eventList +
                '}';
    }

}
