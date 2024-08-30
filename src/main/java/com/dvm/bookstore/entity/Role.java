package com.dvm.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;
    @Enumerated(EnumType.STRING)
    private ERole roleName;
}
