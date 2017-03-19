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
public class Compound implements Serializable {

    @Id
    private int id;
    private String name;
    private String brutto;
    private String smiles;
    @Column(name = "experimental_factor")
    private Double experimentalFactor;
    @Column(name = "prediction_factor")
    private Double predictionFactor;

}
