package com.zeptopluse.entity;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="customers") @Getter @Setter @NoArgsConstructor
public class Customer { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=120) private String name; @Column(nullable=false,unique=true,length=20) private String phone; @Column(unique=true,length=160) private String email; @Column(nullable=false,updatable=false) private LocalDateTime createdAt; @PrePersist void created(){createdAt=LocalDateTime.now();} }
