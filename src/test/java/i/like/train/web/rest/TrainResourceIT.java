package i.like.train.web.rest;

import i.like.train.domain.Train;
import i.like.train.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class TrainResourceIT {

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
    public static Train createEntity(EntityManager em) {
        return new Train().maxNumberOfPassenger(DEFAULT_NUMBER_OF_PASSENGER);
    }

    @BeforeEach
    public void initTest() {
        train = createEntity(em);
    }

    /**
     * Integration tests
     */
    @Test
    public void createTrain() throws Exception {
        final int databaseBeforeCreation = trainRepository.findAll().size();

        restTrainMockMvc.perform(post("/api/trains")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(train))
        ).andExpect(status().isCreated());

        List<Train> trainList = trainRepository.findAll();
        assertThat(trainList).hasSize(databaseBeforeCreation + 1);
        Train trainFromDb = trainList.get(trainList.size() - 1);
        assertThat(trainFromDb.getMaxNumberOfPassenger()).isEqualTo(DEFAULT_NUMBER_OF_PASSENGER);
    }

    @Test
    public void getAllTrains() throws Exception {
        trainRepository.saveAndFlush(train);

        restTrainMockMvc.perform(get("/api/trains")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(train.getId())));
    }

    @Test
    public void getTrainBySpecificId() throws Exception {
        trainRepository.saveAndFlush(train);

        //Debuging like old time lol
        System.out.println(trainRepository.findAll());

        restTrainMockMvc.perform(get("/api/trains/" + train.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(train.getId()))
            .andExpect(jsonPath("$.maxNumberOfPassenger").value(DEFAULT_NUMBER_OF_PASSENGER))
            .andExpect(jsonPath("$.passengerList").isEmpty())
            .andExpect(jsonPath("$.eventList").isEmpty());
    }

    @Test
    public void deleteTrain() throws Exception {
        trainRepository.saveAndFlush(train);
        int databaseBeforeUpdate = trainRepository.findAll().size();

        restTrainMockMvc.perform(delete("/api/trains/" + train.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent());

        List<Train> trainList = trainRepository.findAll();
        assertThat(trainList).hasSize(databaseBeforeUpdate - 1);
    }

}
