package org.cloudbus.cloudsim.datacenter.la;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.LinkedList;
import java.util.List;

public class VmLearningAutomata extends Vm {  // 扩展Vm类实现学习自动机的虚拟机

    private int r = 3;

    private Double[] learningAutomataProbability = {1.0 / 3, 1.0 / 3, 1.0 / 3};
    private String[] action = {"INTA", "DNTA", "NAT"};
    private int currentAction = 0;

    private double a;
    private double b;

    private double previousTime;

    private final List<Double> utilizationHistory = new LinkedList<>();

    public static final int HISTORY_LENGTH = 30;

    public VmLearningAutomata(
            final int id,
            final int userId,
            final double mips,
            final int numberOfPes,
            final int ram,
            final long bw,
            final long size,
            final String vmm,
            final CloudletScheduler cloudletScheduler) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
        setA(0.1);
        setB(0.1);
    }

    // 更新动作
    public void updateAction() {
        int action = 0;
        double value = 0;
        // 随机选择更新
        for (int i = 0; i < r; i++) {
            double randomValue = Math.random() * learningAutomataProbability[i];
            if (randomValue > value) {
                action = i;
                value = randomValue;
            }
        }
        setAction(action);
    }

    private void setAction(int action) {
        currentAction = action;
    }

    public String getAction() {
        return action[currentAction];
    }

    void rewardAction(int action) {

        learningAutomataProbability[action] *= (1 - getA());
        learningAutomataProbability[action] += getA();

        for (int i = 0; i < r; i++) {

            if (i != action) learningAutomataProbability[i] *= (1 - getA());
        }
    }

    void penalizeAction(int action) {

        learningAutomataProbability[action] *= (1 - getB());

        for (int i = 0; i < r; i++) {
            if (i != action) {

                learningAutomataProbability[i] *= (1 - getB());
                learningAutomataProbability[i] += getB() / (r - 1);
            }
        }
    }

    public void updateLA() {

        double mean = getUtilizationMean();
        double current = getTotalUtilizationOfCpuMips(CloudSim.clock());

        switch (getAction()) {

            case "NAT":
                if (mean == current)
                    rewardAction(currentAction);
                else
                    penalizeAction(currentAction);
                break;
            case "DNTA":
                if (mean < current)
                    rewardAction(currentAction);
                else
                    penalizeAction(currentAction);
                break;
            case "INTA":
                if (mean > current)
                    rewardAction(currentAction);
                else
                    penalizeAction(currentAction);
                break;

        }
    }

    public Double[] getLearningAutomataProbability() {
        return learningAutomataProbability;
    }

    public int getCurrentAction() {
        return currentAction;
    }

    public double getPreviousTime() {
        return previousTime;
    }

    @Override
    public double updateVmProcessing(final double currentTime, final List<Double> mipsShare) {

        double time = super.updateVmProcessing(currentTime, mipsShare);

        if (currentTime > getPreviousTime()) {
            double utilization = getTotalUtilizationOfCpu(getCloudletScheduler().getPreviousTime());
            if (utilization > 1) utilization = 1;
            if (CloudSim.clock() != 0) {
                addUtilizationHistoryValue(utilization);
            }
            setPreviousTime(currentTime);
        }
        return time;
    }

    private void setA(double a) {
        this.a = a;
    }

    private void setB(double b) {
        this.b = b;
    }

    private double getA() {
        return this.a;
    }

    private double getB() {
        return this.b;
    }

    public void addUtilizationHistoryValue(final double utilization) {
        getUtilizationHistory().add(0, utilization);
        if (getUtilizationHistory().size() > HISTORY_LENGTH) {
            getUtilizationHistory().remove(HISTORY_LENGTH);
        }
    }

    protected List<Double> getUtilizationHistory() {
        return utilizationHistory;
    }

    public void setPreviousTime(final double previousTime) {
        this.previousTime = previousTime;
    }

    public double getUtilizationVariance() {

        double mean = getUtilizationMean();
        double variance = 0;
        
        if (!getUtilizationHistory().isEmpty()) {
            int n = HISTORY_LENGTH;
            if (HISTORY_LENGTH > getUtilizationHistory().size()) {
                n = getUtilizationHistory().size();
            }
            for (int i = 0; i < n; i++) {
                double tmp = getUtilizationHistory().get(i) * getMips() - mean;
                variance += tmp * tmp;
            }
            variance /= n;
        }
        return variance;
    }

    public double getUtilizationMean() {
        double sum = 0;
        if (!getUtilizationHistory().isEmpty()) {
            int n = HISTORY_LENGTH;
            if (HISTORY_LENGTH > getUtilizationHistory().size()) {
                n = getUtilizationHistory().size();
            }
            for (int i = 0; i < n; i++) {
                sum += getUtilizationHistory().get(i) * getMips();
            }
            return sum / n;
        }
        return sum;
    }
}
