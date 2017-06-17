package ru.pavlik.chempred.server.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.neuroph.core.NeuralNetwork;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table
@Getter
@Setter
public class NeuralNetworkModel implements Serializable {

    @Id
    private int id;
    @Column(name = "neural_network")
    @Type(type = "java.io.Serializable")
    private NeuralNetwork neuralNetwork;
    @Column(name = "current_iteration")
    private int currentIteration;
    @Column(name = "total_error")
    private double totalError;

    protected NeuralNetworkModel() {
    }

    public NeuralNetworkModel(TypeId id) {
        this.id = id.ordinal();
    }

    public enum TypeId {
        LEL, UEL
    }
}
