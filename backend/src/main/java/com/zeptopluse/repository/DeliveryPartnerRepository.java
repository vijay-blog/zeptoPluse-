package com.zeptopluse.repository;
import com.zeptopluse.entity.DeliveryPartner; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner,Long> { Optional<DeliveryPartner> findByEmailIgnoreCase(String email); Optional<DeliveryPartner> findByPhone(String phone); List<DeliveryPartner> findAllByOrderByCreatedAtDesc(); }
