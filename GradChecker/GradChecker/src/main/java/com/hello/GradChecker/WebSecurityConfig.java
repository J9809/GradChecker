package com.hello.GradChecker;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/hello")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    private static class UserDetailsServiceImpl implements UserDetailsService {

        private final MongoCollection<Document> usersCollection;

        public UserDetailsServiceImpl() {
            // MongoDB에 연결하여 컬렉션 초기화
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("GradChecker");
            usersCollection = database.getCollection("students");
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // MongoDB에서 사용자 정보 확인
            Document user = usersCollection.find(new Document("student_id", username)).first();
            if (user != null) {
                // 사용자 정보가 있으면 UserDetails 객체를 생성하여 반환
                String encoded_pw = user.getString("pw");

                System.out.println("가져온 아이디: " + username);
                System.out.println("가져온 비밀번호: " + encoded_pw);

                return org.springframework.security.core.userdetails.User.withUsername(username)
                        .password(encoded_pw)
                        //.roles("USER")
                        .build();
            } else {
                // 사용자 정보가 없으면 예외 발생
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }
    }
}