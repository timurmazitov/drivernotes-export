package ru.mazitov.timur.drivernotes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements ApplicationRunner {
	@Autowired
	private ExportService exportService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		this.checkOptionExists(args, "login");
		this.checkOptionExists(args, "password");
		this.checkOptionExists(args, "output");
		this.checkOptionExists(args, "outputTypes");
		exportService.export();
	}

	private void checkOptionExists(ApplicationArguments args, String name) throws ExportException {
		List<String> values = args.getOptionValues(name);
		if (values == null || values.isEmpty()) {
			throw new ExportException(String.format("No '%s' argument defined", name));
		}
	}
}
