package i.like.train.web.rest;

import i.like.train.domain.Train;
import i.like.train.helper.ResponseUtil;
import i.like.train.repository.TrainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class TrainResource {

    private final Logger log = LoggerFactory.getLogger(TrainResource.class);

    private final TrainRepository repository;

    public TrainResource(TrainRepository repository) {
        this.repository = repository;
    }

    /**
     * {@code POST /trains} : Create a new Train
     *
     * @return {@link ResponseEntity} with status {@code 201 (Created} or with status {@code 400 (Bad Request)}
     */
    @PostMapping("/trains")
    public ResponseEntity<Train> createTrain(@RequestBody Train trainToSaveOnDatabase) throws URISyntaxException {
        log.debug("REST request to save Train");
        Train result = repository.save(trainToSaveOnDatabase);
        return ResponseEntity.created(new URI("/api/trains/" + result.getId()))
                .body(result);
    }

    /**
     * GET all trains from database
     */
    @GetMapping("/trains")
    public ResponseEntity<List<Train>> getAllTrains() {
        log.debug("REST request to get all Trains");
        List<Train> trainList = repository.findAll();
        return ResponseEntity.ok().body(trainList);
    }

    /**
     * GET a specific train from database based on his id
     */
    @GetMapping("/trains/{id}")
    public ResponseEntity<Train> getTrainById(@PathVariable Long id) {
        log.debug("REST request to get Train: {}", id);
        Optional<Train> train = repository.findById(id);
        return ResponseUtil.wrapOrNotFound(train);
    }

}
