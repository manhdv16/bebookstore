package com.dvm.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Entity for Category
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;
    private String categoryName;
    @Column(length = 1000)
    private String description;
    @OneToMany(mappedBy = "category",cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Book> listBooks;
}
