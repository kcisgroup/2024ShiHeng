package org.cloudbus.cloudsim.datacenter.la;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class LAAlgorithm { 

    private final int actionIncrease = 0;  
    private final int actionDecrease = 1;  
    private final int actionNone = 2; 
    Random random = new Random();

    private double higherUtilizationThreshold = 0.8;
    private double lowerUtilizationThreshold = 0.2;

    private void updateLearningAutomataActions(VmLearningAutomata vm) {
        if (isHostOverUtilized(vm)) {
            vm.updateAction();
            vm.updateLA();
        } else {
            vm.updateAction();
            vm.updateLA();
        }
    }

    protected boolean isHostOverUtilized(VmLearningAutomata vm) {

        double totalRequestedMips = 0;
        double predictedUtilization = 0;

        totalRequestedMips += vm.getCurrentRequestedTotalMips();

        double avg = vm.getUtilizationMean() / vm.getMips(); 

        double standardDeviation = Math.sqrt(vm.getUtilizationVariance()) / vm.getMips(); 

        if(vm.getAction().equals("INTA")){ 
            predictedUtilization += (avg + standardDeviation);
        }
        else if(vm.getAction().equals("DNTA")){
            predictedUtilization -= (avg - standardDeviation);
        }

        double utilization = totalRequestedMips / vm.getMips();
       
        return utilization > getUtilizationThreshold() && predictedUtilization > 1 - getUtilizationThreshold();
       
    }

    protected double getUtilizationThreshold() {
        return higherUtilizationThreshold;
    }

    protected double getLowerUtilizationThreshold() {
        return lowerUtilizationThreshold;
    }

    private int selectVm(List<Vm> vmList) {  

        int r=random.nextInt(vmList.size());
        VmLearningAutomata vm = (VmLearningAutomata) vmList.get(r);
        double utilization = getPredictedUtilization(vm);

        if (utilization >= lowerUtilizationThreshold && utilization <= higherUtilizationThreshold){
            return r;
        }

        return 0;
    }




    private double getPredictedUtilization(Vm vm) {  

        double mean = vm.getUtilizationMean(); 
        double currentUtilization = vm.getTotalUtilizationOfCpuMips(CloudSim.clock());  

        return currentUtilization + (mean - currentUtilization);  
    }

    private double calculateMeanUtilization(Host host) {  

        double totalMips = host.getTotalMips();
        double meanUtilization = 0;

        for (VmLearningAutomata vm : host.<VmLearningAutomata>getVmList()) {  
            double vmMips = vm.getMips();
            double vmUtilization = vmMips / totalMips;  
            meanUtilization += vmUtilization;  
        }

        return meanUtilization / host.getVmList().size(); 
    }
}
