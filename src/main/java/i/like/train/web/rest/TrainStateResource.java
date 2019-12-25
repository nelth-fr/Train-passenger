package i.like.train.web.rest;

import com.google.common.collect.Iterables;
import i.like.train.domain.Event;
import i.like.train.domain.Passenger;
import i.like.train.domain.Train;
import i.like.train.repository.TrainRepository;
import i.like.train.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for complex state operation on {@link Train} entity
 */
@Controller
@RequestMapping("/api")
public class TrainStateResource {

    private final Logger log = LoggerFactory.getLogger(TrainStateResource.class);

    private static final String ENTITY_NAME = "train_state";

    private TrainRepository trainRepository;

    public TrainStateResource(TrainRepository trainRepository){
        this.trainRepository = trainRepository;
    }

    /**
     * {@code GET /trains/{id}/passengers} : Get Passengers from a trains by it's ID
     */
    @GetMapping("/trains/{id}/passengers")
    public ResponseEntity<List<Passenger>> getPassengerListByTrain(@PathVariable Long id) {
        log.debug("REST request to get a list of Passengers on Train.getId() : {}", id);
        Optional<Train> trainRepositoryById = trainRepository.findById(id);
        if(trainRepositoryById.isPresent()) {
            List<Passenger> passengerList = trainRepositoryById.get().getPassengerList();
            return ResponseEntity.ok().body(passengerList);
        }
        throw new BadRequestAlertException("ID unknown, you can't get Passengers from a Train which does not exist", ENTITY_NAME, id.toString());
    }

    /**
     * {@code GET /trains/{id}/events} : Get Events from a trains by it's ID
     *
     * Should be transactional to let the ORM play nicely with database
     */
    @GetMapping("/trains/{id}/events")
    public ResponseEntity<List<Event>> getEventListByTrain(@PathVariable Long id) {
        log.debug("REST request to get a list of Events on Train.getId() : {}", id);
        Optional<Train> trainRepositoryById = trainRepository.findById(id);
        if(trainRepositoryById.isPresent()){
            List<Event> eventList = trainRepositoryById.get().getEventList();
            return ResponseEntity.ok().body(eventList);
        }
        throw new BadRequestAlertException("ID unknown, you can't get Events from a Train which does not exist", ENTITY_NAME, id.toString());
    }

    /**
     * {@code POST /trains/{id}/events}
     */
    @Transactional
    @PostMapping("/trains/{id}/events")
    public ResponseEntity<Event> createNewEventByTrain(@PathVariable Long id) throws URISyntaxException, NullPointerException {
        log.debug("REST request to Post a new Event on Train.getId() : {}", id);
        Optional<Train> train = trainRepository.findById(id);
        // We use the power of Optional type or throw exception
        if(train.isPresent()){
            int lastVersion = train.get().getVersion();
            train.get().setVersion(lastVersion + 1);

            List<Event> eventList = train.get().getEventList();
            Event eventToSave = new Event(1);
            if(eventList.size() >= 1) {
                int lastEventVersion = Iterables.getLast(eventList).getVersion();
                eventToSave = new Event(lastEventVersion + 1);
            }

            eventList.add(eventToSave);
            trainRepository.save(train.get());

            return ResponseEntity.created(
                    new URI("/api/trains/" + train.get().getId() + "/events" + eventToSave.getId()))
                    .body(eventToSave);
        }
        throw new BadRequestAlertException("ID unknown, you can't add Event to a Train which does not exist", ENTITY_NAME, id.toString());
    }

    /**
     * {@code DELETE /trains/{id}/events}
     */
    @Transactional
    @DeleteMapping("/trains/{id}/events")
    public ResponseEntity<Event> deleteLastEventByTrain(@PathVariable Long id) throws NullPointerException {
        log.debug("REST request to delete last Event by Train Id : {}", id);
        Optional<Train> train = trainRepository.findById(id);
        // We use the power of Optional type or throw exception
        if(train.isPresent()){
            int currentVersion = train.get().getVersion();
            train.get().setVersion(currentVersion - 1);

            List<Event> eventList = train.get().getEventList();
            Event lastEvent = Iterables.getLast(eventList);
            eventList.remove(lastEvent);

            trainRepository.save(train.get());
            return ResponseEntity.noContent().build();
        }
        throw new BadRequestAlertException("ID unknown, you can't delete the last Event from a Train which does not exist", ENTITY_NAME, id.toString());
    }

//    /**
//     * {@code DELETE /trains/{trainId}/events/{eventId}}
//     */
//    @Transactional
//    @DeleteMapping("/trains/{trainId}/events/{eventId}")
//    public ResponseEntity<Void> delete_until_a_specific_event_on_a_train(@PathVariable Long trainId, @PathVariable Long eventId) throws URISyntaxException, NullPointerException {
//        log.debug("REST request to delete Event by Train Id: {} or eventId: {}", trainId, eventId);
//        Optional<Train> train = trainRepository.findById(trainId);
//
//        if(train.isPresent()){
//            List<Event> eventList = train.get().getEventList();
//            // Sort list then remove every object until reach the eventId
//
//            int trainCurrentVersion = train.get().getVersion();
//            // Remove version until reach the eventVersion
//
//
//            return ResponseEntity.noContent().build();
//        }
//        throw new BadRequestAlertException("ID unknown, you can't delete Event from a Train which does not exist", ENTITY_NAME, trainId.toString());
//    }

}
