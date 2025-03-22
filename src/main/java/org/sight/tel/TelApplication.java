package org.sight.tel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelApplication {

  public static void main(String[] args) {
    SpringApplication.run(TelApplication.class, args);
  }
}
