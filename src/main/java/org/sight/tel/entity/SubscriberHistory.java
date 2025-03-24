package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "subscribers_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
public class SubscriberHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String channelName;

  @Column(nullable = false)
  private String channelUrl;

  @Column(name = "date", nullable = false, columnDefinition = "date")
  private LocalDate date;

  @Column(nullable = false)
  private Integer subscriberCount;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

  public SubscriberHistory(
      String channelName, String channelUrl, LocalDate date, Integer subscriberCount) {
    this.channelName = channelName;
    this.channelUrl = channelUrl;
    this.date = date;
    this.subscriberCount = subscriberCount;
  }
}
