package ru.mazitov.timur.drivernotes;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import ru.mazitov.timur.drivernotes.model.Car;

@Component
public class JsonPrinter {
	private static Logger log = LoggerFactory.getLogger(JsonPrinter.class);

	public void print(String path, Car car) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

		try {
			File file = new File(path + car.getName().replace(" ", "_") + ".json");
			log.info("Exporting json: {}", file.getAbsolutePath());
			objectMapper.writeValue(file, car);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
