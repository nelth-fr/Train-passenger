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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void setup() {
        MockitoAnnotations.initMocks(this);
        final TrainStateResource trainStateResource = new TrainStateResource(trainRepository);
        this.restTrainMockMvc = MockMvcBuilders.standaloneSetup(trainStateResource)
            .setValidator(validator).build();
    }

    /**
     * Created an entity for this test
     */
    static Train createTrainEntity(EntityManager em) {
        return new Train().maxNumberOfPassenger(DEFAULT_NUMBER_OF_PASSENGER);
    }
    static Event createEventEntity(EntityManager em) {
        return new Event();
    }

    @BeforeEach
    void initTest() {
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
    public void deleteLastEvent() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated());
        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated());

        restTrainMockMvc.perform(delete("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isNoContent());

        Train mockedTrain = trainRepository.findById(train.getId()).get();
        assertThat(mockedTrain).isEqualTo(train);
        assertThat(mockedTrain.getVersion()).isEqualTo(1);

        List<Event> eventList = mockedTrain.getEventList();
        assertThat(eventList.size()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void delete_until_a_specific_event_on_a_train() {
        assert false;
    }

    @Test
    @Transactional
    public void event_creation_interact_with_train_state() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated());

        restTrainMockMvc.perform(post("/api/trains/" + train.getId() + "/events"))
        .andExpect(status().isCreated());

        Train mockedTrain = trainRepository.findById(train.getId()).get();
        assertThat(mockedTrain).isEqualTo(train);
        assertThat(mockedTrain.getVersion()).isEqualTo(2);

        List<Event> eventList = mockedTrain.getEventList();
        assertThat(eventList.size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void newEventShouldCreatePassenger() {
        assert false;
    }

}
