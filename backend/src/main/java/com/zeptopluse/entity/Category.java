package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 80) private String name;
    @Column(nullable = false, unique = true, length = 100) private String slug;
    @Column(length = 500) private String description;
    @Column(length = 500) private String imageUrl;
    @Column(nullable = false) private boolean active = true;
    @Column(nullable = false) private int displayOrder;
}
