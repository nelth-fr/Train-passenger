package i.like.train.web.rest;

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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TrainResourceIT {

    /**
     * Object used for testing purpose
     */
    private static final Integer DEFAULT_NUMBER_OF_PASSENGER = 123;
    private static final Integer UPDATED_NUMBER_OF_PASSENGER = 256;

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TrainResource trainResource = new TrainResource(trainRepository);
        this.restTrainMockMvc = MockMvcBuilders.standaloneSetup(trainResource)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test
     */
    public static Train createEntity(EntityManager em) {
        return new Train()
            .numberOfPassenger(DEFAULT_NUMBER_OF_PASSENGER);
    }

    @BeforeEach
    public void initTest() {
        train = createEntity(em);
    }

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
        assertThat(trainFromDb.getNumberOfPassenger()).isEqualTo(DEFAULT_NUMBER_OF_PASSENGER);
    }

    @Test
    public void updateTrain() throws Exception {
        trainRepository.save(train);

        int databaseBeforeUpdate = trainRepository.findAll().size();
        Train trainToUpdate = trainRepository.findById(train.getId()).get();
        trainToUpdate.setNumberOfPassenger(UPDATED_NUMBER_OF_PASSENGER);

        restTrainMockMvc.perform(put("/api/trains")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trainToUpdate))
        ).andExpect(status().isOk());

        List<Train> trainList = trainRepository.findAll();
        assertThat(trainList).hasSize(databaseBeforeUpdate);
    }

    @Test
    public void deleteTrain() throws Exception {
        trainRepository.save(train);

        int databaseBeforeUpdate = trainRepository.findAll().size();

        restTrainMockMvc.perform(delete("/api/trains/" + train.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent());

        List<Train> trainList = trainRepository.findAll();
        assertThat(trainList).hasSize(databaseBeforeUpdate - 1);
    }

}
