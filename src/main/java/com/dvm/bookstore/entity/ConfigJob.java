package com.dvm.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="config_job")
public class ConfigJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String cron;
    private Integer status;
    private Integer lockAtMost;
    private Integer lockAtLeast;
    private String job;
    private String className;
    private String type;
}
