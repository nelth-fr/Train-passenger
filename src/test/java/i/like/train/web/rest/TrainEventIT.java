package i.like.train.web.rest;

import i.like.train.domain.Event;
import i.like.train.domain.Passenger;
import i.like.train.domain.Train;
import i.like.train.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TrainEventIT {

    /**
     * Object used for testing purpose
     */
    private static final Integer DEFAULT_NUMBER_OF_PASSENGER = 10;

    /**
     * Context attributes
     */
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private EntityManager em;

    @Qualifier("defaultValidator")
    @Autowired
    private Validator validator;

    private MockMvc restTrainMockMvc;

    private Train train;
    private Passenger passenger;
    private Event event;

    /**
     * Context initialisation
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TrainResource trainResource = new TrainResource(trainRepository);
        this.restTrainMockMvc = MockMvcBuilders.standaloneSetup(trainResource)
            .setValidator(validator).build();
    }

    /**
     * Created an entity for this test
     */
    public static Train createTrainEntity(EntityManager em) {
        return new Train().maxNumberOfPassenger(DEFAULT_NUMBER_OF_PASSENGER);
    }
    public static Event createEventEntity(EntityManager em) {
        return new Event();
    }

    @BeforeEach
    public void initTest() {
        train = createTrainEntity(em);
        event = createEventEntity(em);
    }

    /**
     * Integration tests
     */
    @Test
    @Transactional
    public void getAllEventsFromTrain() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events")
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(event)))
        .andExpect(status().isCreated());

        restTrainMockMvc.perform(get("/api/trains/" + train.getId() + "/events")
        .contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].id").value(event.getId()))
        .andExpect(jsonPath("$.[*].version").value(event.getVersion()));
        // TODO : event.createdAt() should be tested but it include to play around with Timestamp and it tooks time --"
    }

    @Test
    @Transactional
    public void createEvent() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(event.getId()))
        .andExpect(jsonPath("$.version").value(0));
//        // TODO .andExpect(jsonPath("$.createdAt").value(event.getCreatedAt().getTime()));
    }

    @Test
    @Transactional
    public void deleteEvent() throws Exception {
        assert false;
    }

    @Test
    @Transactional
    public void train_and_event_states_should_stay_synchronise_over_time() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(get("/api/trains/" + train.getId()))
        .andExpect(status().isOk());

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.version").value(1));

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.version").value(2));

        restTrainMockMvc.perform(get("/api/trains/" + train.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.version").value(2));
        // I dunno why this test fails, I suppose I've forgot something but let's continue
    }

    @Test
    @Transactional
    public void newEventShouldCreatePassenger() {
        assert false;
    }

}
