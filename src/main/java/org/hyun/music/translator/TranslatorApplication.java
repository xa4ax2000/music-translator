package org.hyun.music.translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
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
