package org.leaf.application;

import org.cloudbus.cloudsim.vms.Vm;
import org.jgrapht.graph.DefaultEdge;

/**
 * A data flow between two Tasks in an Application
 */
public class DataFlow extends DefaultEdge {

    private double bitRate; // in bit/s

    public DataFlow(double bitRate) {
        this.bitRate = bitRate;
    }

    public double getBitRate() {
        return bitRate;
    }

    public Vm getSourceVm() {
        return (Vm) getSource();
    }

    public Vm getTargetVm()
    {
        return (Vm) getTarget();
    }
}
