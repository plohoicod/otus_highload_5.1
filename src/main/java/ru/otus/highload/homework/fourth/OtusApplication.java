package ru.otus.highload.homework.fourth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class OtusApplication {
    public static void main(String[] args) throws IOException {

            SpringApplication.run(OtusApplication.class, args);

            /*Faker faker = new Faker(new Locale("ru"));
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            PrintWriter f0 = new PrintWriter(new FileWriter("/Users/ydpolivt/IdeaProjects/otus_highload_2/users.csv"));
            String s = "";
            f0.print("login" + ",");
            f0.print("password" + ",");
            f0.print("name" + ",");
            f0.print("surname" + ",");
            f0.print("birthday" + ",");
            f0.print("gender" + ",");
            f0.print("interests" + ",");
            f0.print("city");
            f0.println();
            String password = passwordEncoder.encode("123");
            for(int i=1;i<=1000000;i++)
            {
                System.out.println(i);
                f0.print("login" + i + ",");
                f0.print(password + ",");
                f0.print(faker.name().firstName() + ",");
                f0.print(faker.name().lastName() + ",");
                f0.print(new Timestamp(faker.date().birthday().getTime()) + ",");
                f0.print("" + ",");
                f0.print("" + ",");
                f0.print(faker.address().city());
                f0.println();
            }
            f0.close();*/
    }

}
