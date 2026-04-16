package com.xxx.ddd.domain.repository;

import com.xxx.ddd.domain.model.entity.TicketDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketDetailRepository extends JpaRepository<TicketDetail, Long> {
//    Optional<TicketDetail> findById(Long id);
}
