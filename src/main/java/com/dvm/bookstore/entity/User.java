package com.dvm.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

/**
 * Entity for User
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String userName;
    @JsonIgnore
    private String password;
    private String email;
    private Date created;
    private String address;
    private String phoneNumber;
    private String otp;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "User_Role",joinColumns = @JoinColumn(name = "userId"),
    inverseJoinColumns = @JoinColumn(name = "roleId"))
    private Set<Role> listRoles;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> listOrders;
}
