package org.cloudbus.cloudsim.datacenter.la;

import org.cloudbus.cloudsim.datacenter.datacenter.Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

public class LAScheduler extends Scheduler {

    private LAAlgorithm laAlgorithm;

    public LAScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {

        super(cloudletList, vmList);

        this.laAlgorithm= new LAAlgorithm();

        Log.printLine("Using Learning Automata Scheduler");
    }

    @Override
    public int[] allocate() {

        return laAlgorithm.run(cloudletList,vmList);

    }
}
