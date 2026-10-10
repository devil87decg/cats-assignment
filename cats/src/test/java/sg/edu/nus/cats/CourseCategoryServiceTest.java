package sg.edu.nus.cats;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import sg.edu.nus.cats.model.CourseCategoryMaster;
import sg.edu.nus.cats.repository.CourseCategoryRepository;
import sg.edu.nus.cats.repository.CourseRepository;
import sg.edu.nus.cats.service.CourseCategoryService;

class CourseCategoryServiceTest {
	private CourseCategoryRepository categories;
	private CourseRepository courses;
	private CourseCategoryService service;

	@BeforeEach
	void setUp() {
		categories = Mockito.mock(CourseCategoryRepository.class);
		courses = Mockito.mock(CourseRepository.class);
		service = new CourseCategoryService(categories, courses);
	}

	@Test
	void create_trimsNameAndUppercasesCode() {
		when(categories.findByCode("EXTERNAL_TEST")).thenReturn(Optional.empty());
		when(categories.findByName("External Test")).thenReturn(Optional.empty());
		when(categories.save(any(CourseCategoryMaster.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CourseCategoryMaster result = service.create(" external_test ", " External Test ", "description", false);

		assertThat(result.getCode()).isEqualTo("EXTERNAL_TEST");
		assertThat(result.getName()).isEqualTo("External Test");
		assertThat(result.isActive()).isTrue();
	}

	@Test
	void create_blankName_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> service.create("VALID_CODE", "   ", "description", false));
	}

	@Test
	void create_duplicateCode_throwsException() {
		CourseCategoryMaster existing = new CourseCategoryMaster();
		existing.setCode("INTERNAL");
		existing.setName("Internal Training");
		when(categories.findByCode("INTERNAL")).thenReturn(Optional.of(existing));

		assertThrows(IllegalArgumentException.class, () -> service.create("internal", "Another Name", null, true));
	}
}
