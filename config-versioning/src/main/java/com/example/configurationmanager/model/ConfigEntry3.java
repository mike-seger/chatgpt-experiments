package com.example.configurationmanager;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "config_table3")
public class ConfigEntry3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;
}
