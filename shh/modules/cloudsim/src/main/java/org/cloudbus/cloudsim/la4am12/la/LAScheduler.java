package org.cloudbus.cloudsim.la4am12.la;

import org.cloudbus.cloudsim.la4am12.datacenter.Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.la4am12.datacenter.Scheduler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LAScheduler extends Scheduler {
    private LAAlgorithm laAlgorithm;

    public LAScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
        super(cloudletList, vmList);
        //Collections.sort(cloudletList, Comparator.comparingLong(Cloudlet::getCloudletLength));//小到大
        //Collections.sort(vmList, Comparator.comparingDouble(Vm::getMips));//小到大
        //Collections.reverse(cloudletList);
        //Collections.reverse(vmList);
        this.laAlgorithm= new LAAlgorithm();

        Log.printLine("Using Learning Automata Scheduler");
    }

    @Override
    public int[] allocate() {

        return laAlgorithm.run(cloudletList,vmList);

    }
}
