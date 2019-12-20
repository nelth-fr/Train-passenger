package i.like.train.web.rest;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
    public ResponseEntity<Train> createTrain(@RequestBody Train trainToSaveOnDatabase) throws URISyntaxException {
        log.debug("REST request to save Train : {}", trainToSaveOnDatabase);
        if(trainToSaveOnDatabase.getId() != null) {
            throw new BadRequestAlertException("A new Train cannot already have an ID", ENTITY_NAME, "id-exists");
        }
        Train result = trainRepository.save(trainToSaveOnDatabase);
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
        log.debug("REST request to get a list of Passenger by Trains : {}", id);
        Optional<Train> trainRepositoryById = trainRepository.findById(id);
        List<Passenger> passengerList = trainRepositoryById.get().getPassengerList();
        return ResponseEntity.ok().body(passengerList);
    }

    /**
     * {@code GET /trains/{id}/events} : Get Events from a trains by it's ID
     *
     * Should be transactional to let the ORM play nicely with database
     */
    @Transactional
    @GetMapping("/trains/{id}/events")
    public ResponseEntity<List<Event>> getEventListByTrain(@PathVariable Long id) {
        log.debug("REST request to get a list of Events by Trains : {}", id);
        Optional<Train> trainRepositoryById = trainRepository.findById(id);
        List<Event> eventList = trainRepositoryById.get().getEventList();
        return ResponseEntity.ok().body(eventList);
    }

    /**
     * {@code GET /trains/{id}/events} : Get Events from a trains by it's ID
     *
     * Should be transactional to let the ORM play nicely with database
     */
    @Transactional
    @PostMapping("/trains/{id}/events")
    public ResponseEntity<Event> createNewEventByTrain(@PathVariable Long id, @RequestBody Event eventToSave) throws URISyntaxException {
        log.debug("REST request to get a list of Passenger by Trains : {}", id);
        Train train = trainRepository.findById(id).get();
        List<Event> trainEventList = train.getEventList();
        trainEventList.add(eventToSave);

        trainRepository.save(train);
        return ResponseEntity
                .created(URI.create(new URI("/api/trains/" + train.getId()) + "/events/" + eventToSave.getId()))
                .body(eventToSave);
    }

}
