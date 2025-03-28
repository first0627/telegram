package org.sight.tel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.sight.tel.entity.SubscriberHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberHistoryRepository extends JpaRepository<SubscriberHistory, Long> {

  Optional<SubscriberHistory> findByChannelNameAndDate(String channelName, LocalDate date);

  List<SubscriberHistory> findByChannelNameAndDateBetween(
      String channelName, LocalDate start, LocalDate end);

  @Query(
      """
    SELECT sh FROM SubscriberHistory sh
    JOIN FETCH sh.channel c
    WHERE sh.date BETWEEN :start AND :end
  """)
  List<SubscriberHistory> findWithChannelBetweenDates(
      @Param("start") LocalDate start, @Param("end") LocalDate end);
}
