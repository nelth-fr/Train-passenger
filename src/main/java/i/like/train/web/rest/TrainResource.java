package i.like.train.web.rest;

import com.google.common.collect.Iterables;
import i.like.train.domain.Event;
import i.like.train.domain.Passenger;
import i.like.train.domain.Train;
import i.like.train.repository.TrainRepository;
import i.like.train.web.rest.error.BadRequestAlertException;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.OptionalDataException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class TrainResource {

    private final Logger log = LoggerFactory.getLogger(TrainResource.class);

    private static final String ENTITY_NAME = "train";

    private TrainRepository trainRepository;

    public TrainResource(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    /**
     * {@code POST /trains} : Create a new Train
     */
    @PostMapping("/trains")
    public ResponseEntity<Train> createTrain(@RequestBody Train train) throws URISyntaxException {
        log.debug("REST request to save Train : {}", train);
        if(train.getId() != null) {
            throw new BadRequestAlertException("A new Train cannot already have an ID", ENTITY_NAME, "id-exists");
        }
        Train result = trainRepository.save(train);
        return ResponseEntity.created(new URI("/api/trains/" + result.getId()))
            .body(result);
    }

    /**
     * {@code DELETE /trains/{id}} : Delete a new Train by it's ID
     */
    @DeleteMapping("/trains/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        log.debug("REST request to delete train by Id : {}", id);
        trainRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /trains} : Get all Trains
     */
    @GetMapping("/trains")
    public ResponseEntity<List<Train>> getAllTrains() {
        log.debug("REST request to get all Trains");
        List<Train> trainList = trainRepository.findAll();
        return ResponseEntity.ok().body(trainList);
    }

    /**
     * {@code GET /trains/{id}} : Get trains by it's ID
     */
    @GetMapping("/trains/{id}")
    public ResponseEntity<Train> getTrainById(@PathVariable Long id) {
        log.debug("REST request to get Train: {}", id);
        Optional<Train> train = trainRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(train);
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
        List<Event> eventList = trainRepositoryById.get().getEventList();
        return ResponseEntity.ok().body(eventList);
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

}
