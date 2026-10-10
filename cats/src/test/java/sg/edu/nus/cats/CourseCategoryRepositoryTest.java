package sg.edu.nus.cats;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import sg.edu.nus.cats.model.CourseCategoryMaster;
import sg.edu.nus.cats.repository.CourseCategoryRepository;

@DataJpaTest
@ActiveProfiles("jpa-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CourseCategoryRepositoryTest {

	@Autowired
	private CourseCategoryRepository categories;

	@Test
	void saveCategory_thenFindByCode() {
		CourseCategoryMaster category = new CourseCategoryMaster();
		category.setCode("TEST_CAT");
		category.setName("Test Category");
		category.setDescription("Created by a JPA test");
		category.setInternalTraining(false);
		category.setActive(true);

		CourseCategoryMaster saved = categories.saveAndFlush(category);

		assertThat(saved.getId()).isNotNull();
		assertThat(categories.findByCode("TEST_CAT")).isPresent().get().extracting(CourseCategoryMaster::getName)
				.isEqualTo("Test Category");
	}

	@Autowired
	private javax.sql.DataSource dataSource;

	@Test
	void verifyDatabaseConnection() throws Exception {

		try (var connection = dataSource.getConnection()) {

			String databaseName = connection.getCatalog();

			System.out.println("Connected database: " + databaseName);

			assertThat(databaseName).isEqualTo("cats_test");
		}
	}

}
