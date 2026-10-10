package sg.edu.nus.cats;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
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
class CourseCategoryActiveQueryTest {
	@Autowired
	private CourseCategoryRepository categories;

	@BeforeEach
	void setUp() {
		CourseCategoryMaster active = new CourseCategoryMaster();
		active.setCode("ACTIVE_TEST");
		active.setName("Active Test Category");
		active.setActive(true);
		categories.save(active);

		CourseCategoryMaster inactive = new CourseCategoryMaster();
		inactive.setCode("INACTIVE_TEST");
		inactive.setName("Inactive Test Category");
		inactive.setActive(false);
		categories.save(inactive);
		categories.flush();
	}

	@Test
	void findByActiveTrue_returnsOnlyActiveCategories() {
		var result = categories.findByActiveTrue();

		assertThat(result).extracting(CourseCategoryMaster::getCode).contains("ACTIVE_TEST")
				.doesNotContain("INACTIVE_TEST");
	}
}
