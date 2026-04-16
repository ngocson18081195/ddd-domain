package com.xxx.ddd.infrastructure.persistency.mapper;

import com.xxx.ddd.domain.model.entity.TicketDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketOrderJPAMapper extends JpaRepository<TicketDetail, Long> {

    @Modifying
    @Transactional
    @Query("update TicketDetail t set t.updatedAt = current_timestamp, " +
            "t.stockAvailable = t.stockAvailable - :quantity " +
            "where t.id = :ticketId")
    int decreaseStockLevel0(@Param("ticketId") Long ticketId, @Param("quantity") int quantity);

    @Modifying
    @Transactional
    @Query("update TicketDetail t set t.updatedAt = current_timestamp, " +
            "t.stockAvailable = t.stockAvailable - :quantity " +
            "where t.id = :ticketId and t.stockAvailable >= :quantity")
    int decreaseStockLevel1(@Param("ticketId") Long ticketId, @Param("quantity") int quantity);

    @Modifying
    @Transactional
    @Query("update TicketDetail t set t.updatedAt = current_timestamp, " +
            "t.stockAvailable = :oldStockAvailable - :quantity " +
            "where t.id = :ticketId and t.stockAvailable = :oldStockAvailable")
    int decreaseStockLevel3CAS(@Param("ticketId") Long ticketId,@Param("oldStockAvailable") int oldStockAvailable,@Param("quantity") int quantity);

    @Query("SELECT t.stockAvailable from TicketDetail t where t.id = :ticketId")
    public int getStockAvailable(@Param("ticketId") Long ticketId);

}
