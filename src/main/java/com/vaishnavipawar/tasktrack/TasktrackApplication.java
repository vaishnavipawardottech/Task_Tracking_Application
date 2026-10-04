package com.vaishnavipawar.tasktrack;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Time;
import java.util.TimeZone;

@SpringBootApplication
public class TasktrackApplication {

	public static void main(String[] args) {
        // set default timezone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        // Configuration of dotenv-java
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        dotenv.entries().forEach((DotenvEntry entry) -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(TasktrackApplication.class, args);
	}

}
