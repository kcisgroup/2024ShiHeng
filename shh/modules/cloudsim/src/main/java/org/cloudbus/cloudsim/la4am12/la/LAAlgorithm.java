package org.cloudbus.cloudsim.la4am12.la;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.*;

public class LAAlgorithm {  // 定义学习自动机算法类

    private final int actionIncrease = 0;  // 增加操作的标识
    private final int actionDecrease = 1;  // 减少操作的标识
    private final int actionNone = 2;  // 不执行操作的标识

    Random random = new Random();

    private double higherUtilizationThreshold = 0.8;// 利用率阈值
    private double lowerUtilizationThreshold = 0.2;

    public int[] run(List<Cloudlet> cloudletList, List<Vm> vmList) {

        int[] cloudletToVm = new int[cloudletList.size()];

        // 遍历每个Cloudlet
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);

            // 选择一个虚拟机
            int selectedVmIndex = selectVm(vmList);

            // 获取选择的虚拟机并更新学习自动机的行动
            VmLearningAutomata selectedVm = (VmLearningAutomata) vmList.get(selectedVmIndex);
            updateLearningAutomataActions(selectedVm);


            // 将Cloudlet分配给选定的虚拟机
            cloudletToVm[i] = selectedVmIndex;
        }

        return cloudletToVm;
    }


    private void updateLearningAutomataActions(VmLearningAutomata vm) {
        // 如果主机过载，更新学习自动机的行动
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

        totalRequestedMips += vm.getCurrentRequestedTotalMips();// 获取当前总请求Mips

        // 计算每个虚拟机的平均利用率和标准差
        double avg = vm.getUtilizationMean() / vm.getMips(); // 获取每个VM的平均利用率

        double standardDeviation = Math.sqrt(vm.getUtilizationVariance()) / vm.getMips(); // 获取每个VM的标准差

        // 根据虚拟机的行动（增加或减少利用率）更新预测的总利用率
        if(vm.getAction().equals("INTA")){ // 如果虚拟机的行动是增加利用率
            predictedUtilization += (avg + standardDeviation);
        }
        else if(vm.getAction().equals("DNTA")){// 如果虚拟机的行动是减少利用率
            predictedUtilization -= (avg - standardDeviation);
        }

        double utilization = totalRequestedMips / vm.getMips();// 计算当前主机的CPU利用率
        // 满足两个条件满足
        return utilization > getUtilizationThreshold() && predictedUtilization > 1 - getUtilizationThreshold();
        // 如果当前利用率和预测利用率都大于利用率阈值的补数，则表示主机被过度使用。否则表示主机没有被过度使用。
    }

    protected double getUtilizationThreshold() {
        return higherUtilizationThreshold;
    }

    // 获取低利用率阈值。
    protected double getLowerUtilizationThreshold() {
        return lowerUtilizationThreshold;
    }

    private int selectVm(List<Vm> vmList) {  // 随机选择一个虚拟机

        int r=random.nextInt(vmList.size());
        VmLearningAutomata vm = (VmLearningAutomata) vmList.get(r);
        double utilization = getPredictedUtilization(vm);

        // 如果预测利用率在阈值范围内，则选择该虚拟机
        if (utilization >= lowerUtilizationThreshold && utilization <= higherUtilizationThreshold){
            return r;
        }

        // 如果不在范围内，则返回默认选择
        return 0;
    }




    private double getPredictedUtilization(Vm vm) {  // 获取预测的虚拟机CPU利用率

        double mean = vm.getUtilizationMean(); // 计算主机的平均利用率
        double currentUtilization = vm.getTotalUtilizationOfCpuMips(CloudSim.clock());  // 当前虚拟机的利用率

        return currentUtilization + (mean - currentUtilization);  // 预测利用率根据平均利用率和调度间隔计算
    }

    private double calculateMeanUtilization(Host host) {  // 计算主机的平均利用率

        double totalMips = host.getTotalMips();
        double meanUtilization = 0;

        for (VmLearningAutomata vm : host.<VmLearningAutomata>getVmList()) {  // 遍历主机上的所有虚拟机
            double vmMips = vm.getMips();
            double vmUtilization = vmMips / totalMips;  // 计算虚拟机的利用率
            meanUtilization += vmUtilization;  // 累加虚拟机利用率
        }

        return meanUtilization / host.getVmList().size();  // 计算平均利用率
    }
}
