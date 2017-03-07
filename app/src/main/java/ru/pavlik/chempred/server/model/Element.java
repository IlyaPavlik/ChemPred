package ru.pavlik.chempred.server.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table
@Getter
@Setter
@ToString
public class Element implements Serializable {

    @Id
    @Column(name = "atomic_number")
    private Integer id;
    private String symbol;
    private String name;
    private Integer period;
    @Column(name = "molar_mass")
    private Double weight;
    @Column(name = "periodic_group")
    private Integer group;
    private Integer valence;
    private Double electronegativity;

}
