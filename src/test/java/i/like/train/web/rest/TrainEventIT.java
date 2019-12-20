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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TrainEventIT {

    /**
     * Object used for testing purpose
     */
    private static final Integer DEFAULT_NUMBER_OF_PASSENGER = 10;
    private static final List<Passenger> DEFAULT_PASSENGER_LIST = new ArrayList<>();
    private static final List<Event> DEFAULT_EVENT_LIST = new ArrayList<>();

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
    public void createEent() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events")
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(event)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(event.getId()))
        .andExpect(jsonPath("$.version").value(event.getVersion()))
        .andExpect(jsonPath("$.createdAt").value(event.getCreatedAt().getTime()));
    }

    @Test
    @Transactional
    public void newEventShouldInteractWithTrainState() throws Exception {
        trainRepository.saveAndFlush(train);
        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events")
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(event)));

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events")
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(event)));

        Train train = trainRepository.findAll().get(0);
        assertThat(train.getVersion()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void newEventShouldCreatePassenger() {
        assert false;
    }

}
