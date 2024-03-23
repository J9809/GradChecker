package com.gradchecker.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

@Controller
public class LoginController {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> studentsCollection;

    public LoginController() {
        // MongoDB에 연결
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("GradChecker");
        studentsCollection = database.getCollection("students");
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        // MongoDB에서 사용자 정보 확인
        Document student = studentsCollection.find(new Document("student_id", username)).first();
        if (student != null && student.getString("pw").equals(password)) {
            System.out.println("success" + username + password);
            return "success"; // 인증 성공 시 대시보드 페이지로 리다이렉트
        } else {
            System.out.println("fail" + username + password);

            return "redirect:/login?error"; // 인증 실패 시 로그인 페이지로 리다이렉트 (에러 메시지 포함)
        }
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

}
