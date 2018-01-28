package tech.ggsoft.digitplace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DigitPlaceApplication {
	 public static Path downloadedContentDir;
	public static void main(String[] args) throws IOException{
		downloadedContentDir = Files.createTempDirectory("line-bot");
		SpringApplication.run(DigitPlaceApplication.class, args);
	}   
}
