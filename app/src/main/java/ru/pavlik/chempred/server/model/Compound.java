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
    @Column(name = "experimental_lower_factor")
    private Double experimentalLowerFactor;
    @Column(name = "prediction_lower_factor")
    private Double predictionLowerFactor;
    @Column(name = "experimental_upper_factor")
    private Double experimentalUpperFactor;
    @Column(name = "prediction_upper_factor")
    private Double predictionUpperFactor;

}
