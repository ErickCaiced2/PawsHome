package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private String breed;
    private int age;
    private String description;
}
