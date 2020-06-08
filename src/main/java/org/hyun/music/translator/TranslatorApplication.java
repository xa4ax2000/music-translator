package org.hyun.music.translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"org.hyun.music.translator.repository"})
public class TranslatorApplication {

	// Setting Timezone to EST so our DB will be consistent when storing Date
	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("EST"));
	}

	public static void main(String[] args) {
		SpringApplication.run(TranslatorApplication.class, args);
	}

}
