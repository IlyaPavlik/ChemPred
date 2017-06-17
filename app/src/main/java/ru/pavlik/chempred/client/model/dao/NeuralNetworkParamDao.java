package ru.pavlik.chempred.client.model.dao;

import java.io.Serializable;

public class NeuralNetworkParamDao implements Serializable {

    private String activationFunction;
    private double maxError;
    private double rate;
    private double totalIterations;
    private int currentIterations;
    private int inputSize;
    private int outputSize;
    private double totalError;

    public String getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(String activationFunction) {
        this.activationFunction = activationFunction;
    }

    public double getMaxError() {
        return maxError;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getTotalIterations() {
        return totalIterations;
    }

    public void setTotalIterations(double totalIterations) {
        this.totalIterations = totalIterations;
    }

    public int getCurrentIterations() {
        return currentIterations;
    }

    public void setCurrentIterations(int currentIterations) {
        this.currentIterations = currentIterations;
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public double getTotalError() {
        return totalError;
    }

    public void setTotalError(double totalError) {
        this.totalError = totalError;
    }
}
