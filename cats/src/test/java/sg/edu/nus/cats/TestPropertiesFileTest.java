package sg.edu.nus.cats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class TestPropertiesFileTest {

	@Test
	void verifyTestPropertiesFile() throws Exception {

		Properties properties = new Properties();

		try (InputStream input = getClass().getClassLoader().getResourceAsStream("application-jpa-test.properties")) {

			if (input == null) {
				throw new IllegalStateException("application-jpa-test.properties not found");
			}

			properties.load(input);
		}

		String url = properties.getProperty("spring.datasource.url");

		System.out.println("Test database URL: " + url);

		assertEquals("jdbc:mysql://localhost:3306/cats_test", url);
	}
}