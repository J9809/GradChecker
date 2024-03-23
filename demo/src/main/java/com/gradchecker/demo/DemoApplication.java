package com.gradchecker.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.gradchecker.demo.controller.SaveController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		// MongoDB에 연결
		try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
			// 데이터베이스 선택
			MongoDatabase database = mongoClient.getDatabase("GradChecker");

			System.out.println("Connected to the database successfully.");
		} catch (Exception e) {
			System.err.println("An error occurred: " + e);
		}
		// SaveController를 사용하여 데이터 저장
		SaveController.saveData();
	}
}
