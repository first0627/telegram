package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
public class Channel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String urlId; // stock_messenger 같은 ID (t.me/ 뒤에 오는 값)

  private LocalDate createdAt;

  private Integer channelOrder;

  public Channel(String name, String urlId) {
    this.name = name;
    this.urlId = urlId;
    this.createdAt = LocalDate.now(ZoneId.of("Asia/Seoul"));
    this.channelOrder = 0;
  }

  public String getChannelUrl() {
    return "https://t.me/" + urlId;
  }
}