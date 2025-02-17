package com.waleed.profile.profile_backend;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProfileBackendApplication {

	public static void main(String[] args) 
	{
		// this way should work if the .env file is in the root directory
		// it did not work for me so I provided the complete path to .env file

		// Dotenv dotenv = Dotenv.load();

		// dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(),
		// 		entry.getValue()));

		// using the complete .env path
		try 
		{
			Dotenv dotenv = Dotenv.configure().directory("D:/Personal Projects/Profile-Page/Profile-FullStack/profile-backend/.env").load();

            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(),
                    entry.getValue()));

            System.out.println("Environment variables loaded successfully.");
        } 
		catch (Exception e) 
		{
            System.err.println("Error loading .env file: " + e.getMessage());
        }

		SpringApplication.run(ProfileBackendApplication.class, args);
	}

}
