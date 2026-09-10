package com.zeptopluse.repository;
import com.zeptopluse.entity.OrderAssignment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface OrderAssignmentRepository extends JpaRepository<OrderAssignment,Long> { Optional<OrderAssignment> findByOrderId(Long orderId); List<OrderAssignment> findByDeliveryPartnerIdOrderByAssignedAtDesc(Long partnerId); List<OrderAssignment> findByDeliveryPartnerIdAndStatusInOrderByAssignedAtDesc(Long partnerId, Collection<String> statuses); }
