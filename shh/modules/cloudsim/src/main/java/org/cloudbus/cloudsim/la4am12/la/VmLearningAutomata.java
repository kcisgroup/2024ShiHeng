//package org.cloudbus.cloudsim.la4am12.la;
//
//import org.cloudbus.cloudsim.CloudletScheduler;
//import org.cloudbus.cloudsim.Vm;
//import org.cloudbus.cloudsim.core.CloudSim;
//
//import java.util.LinkedList;
//import java.util.List;
//
//public  class VmLearningAutomata extends Vm {  // 扩展Vm类实现学习自动机的虚拟机
//
//    //动作数量
//    private int r = 3;
//    //添加learning automata
//    private Double[] learningAutomataProbability = {1.0/3, 1.0/3, 1.0/3};// 学习自动机的概率分布
//    private String[] action = {"INTA", "DNTA", "NAT"};
//    private int currentAction = 0;
//    //reward, penalty parameter
//    private double a;
//    private double b;
//
//    //////
//    /** The previous time that cloudlets were processed. */
//    private double previousTime;
//    ///
//    private final List<Double> utilizationHistory = new LinkedList<Double>();
//    ///
//    /** The Constant HISTORY_LENGTH. */
//    public static final int HISTORY_LENGTH = 30;
//
//    public VmLearningAutomata(
//            final int id,
//            final int userId,
//            final double mips,
//            final int numberOfPes,
//            final int ram,
//            final long bw,
//            final long size,
//            final String vmm,
//            final CloudletScheduler cloudletScheduler) {
//        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
//        setA(0.1);
//        setB(0.1);
//
//    }
//
//
//    //更新动作
//    public void updateAction(){
//        int action = 0;
//        double value = 0;
//        //update
//        //randomly select
//        for(int i = 0; i < r; i ++){
//            double randomValue = Math.random() * learningAutomataProbability[i];
//            if(randomValue > value) {
//                action = i;
//                value = randomValue;
//            }
//        }
//        setAction(action);
//    }
//
//
//    //set action by int通过int设定动作
//    private void setAction(int action){
//        currentAction = action;
//    }
//
//
//    //get current action in String
//    //获取当前动作的 String 表示
//    public String getAction(){
//        return action[currentAction];
//    }
//
//
//    //action is rewarded 奖励动作
//    void rewardAction(int action){
//        //奖励
//        learningAutomataProbability[action] *= (1-getA());
//        learningAutomataProbability[action] += getA();
//
//        for(int i = 0; i < r; i ++){
//            if(i != action) learningAutomataProbability[i] *= (1-getA());
//        }
//    }
//
//
//    //action is penalized动作是惩罚
//    void penalizeAction(int action){
//        //penalize
//        learningAutomataProbability[action] *= (1-getB());
//
//        for(int i = 0; i < r; i ++){
//            if(i != action) {
//                learningAutomataProbability[i] *= (1-getB());
//                learningAutomataProbability[i] += getB()/(r-1);
//            }
//        }
//    }
//
//
//    public void updateLA() {
//        double mean = getUtilizationMean();
//        double current = getTotalUtilizationOfCpuMips(CloudSim.clock());
//        double temp = getCurrentRequestedTotalMips();
//
//        switch (getAction()) {
//            case "NAT":
//                if (mean == current)
//                    rewardAction(currentAction);
//                else
//                    penalizeAction(currentAction);
//                break;
//            case "DNTA":
//                if (mean < current)
//                    rewardAction(currentAction);
//                else
//                    penalizeAction(currentAction);
//                break;
//            case "INTA":
//                if (mean > current)
//                    rewardAction(currentAction);
//                else
//                    penalizeAction(currentAction);
//                break;
//        }
//    }
//
//
//
//
//
//    public Double[] getLearningAutomataProbability() {  // 获取学习自动机的概率分布
//        return learningAutomataProbability;
//    }
//
//    public int getCurrentAction() {  // 获取当前动作
//        return currentAction;
//    }
//
//
//    ///
//    public double getPreviousTime() {
//        return previousTime;
//    }
//
//    @Override
//    public double updateVmProcessing(final double currentTime, final List<Double> mipsShare) {  // 更新虚拟机的处理过程
//
//        double time = super.updateVmProcessing(currentTime, mipsShare);
//        if (currentTime > getPreviousTime()) {
//            double utilization = getTotalUtilizationOfCpu(getCloudletScheduler().getPreviousTime());
//            //为1
//            if(utilization > 1) utilization = 1;
//            //等于0的时候也得算
//            if (CloudSim.clock() != 0){
//                addUtilizationHistoryValue(utilization);
//            }
//            setPreviousTime(currentTime);
//        }
//        return time;
//    }
//
//    private void setA(double a){
//        this.a = a;
//    }
//    private void setB(double b){
//        this.b = b;
//    }
//
//    private double getA(){
//        return this.a;
//    }
//
//    private double getB(){
//        return this.b;
//    }
//
//    ///
//
//
//
//    ///
//    public void addUtilizationHistoryValue(final double utilization) {
//        getUtilizationHistory().add(0, utilization);
//        if (getUtilizationHistory().size() > HISTORY_LENGTH) {
//            getUtilizationHistory().remove(HISTORY_LENGTH);
//        }
//    }
//
//    ///
//    protected List<Double> getUtilizationHistory() {
//        return utilizationHistory;
//    }
//
//    ///
//    /**
//     * Sets the previous time.
//     *
//     * @param previousTime the new previous time
//     */
//    public void setPreviousTime(final double previousTime) {
//        this.previousTime = previousTime;
//    }
//
//    ///
//    /**
//     * Gets the utilization variance in MIPS.
//     * 获取MIPS中的利用率差异。
//     * @return the utilization variance in MIPS
//     *MIPS中的利用率差异
//     */
//    public double getUtilizationVariance() {
//        double mean = getUtilizationMean();
//        double variance = 0;
//        if (!getUtilizationHistory().isEmpty()) {
//            int n = HISTORY_LENGTH;
//            if (HISTORY_LENGTH > getUtilizationHistory().size()) {
//                n = getUtilizationHistory().size();
//            }
//            for (int i = 0; i < n; i++) {
//                double tmp = getUtilizationHistory().get(i) * getMips() - mean;
//                variance += tmp * tmp;
//            }
//            variance /= n;
//        }
//        return variance;
//    }
//
//
//}
package org.cloudbus.cloudsim.la4am12.la;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.LinkedList;
import java.util.List;

public class VmLearningAutomata extends Vm {  // 扩展Vm类实现学习自动机的虚拟机

    // 动作数量
    private int r = 3;
    // 学习自动机的概率分布
    private Double[] learningAutomataProbability = {1.0 / 3, 1.0 / 3, 1.0 / 3};
    private String[] action = {"INTA", "DNTA", "NAT"};
    private int currentAction = 0;
    // 奖励和惩罚参数
    private double a;
    private double b;

    // 上一次处理cloudlets的时间
    private double previousTime;
    // 利用率历史记录
    private final List<Double> utilizationHistory = new LinkedList<>();

    // 历史记录长度
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

    // 通过int设定动作
    private void setAction(int action) {
        currentAction = action;
    }

    // 获取当前动作的String表示
    public String getAction() {
        return action[currentAction];
    }

    // 奖励动作
    void rewardAction(int action) {
        learningAutomataProbability[action] *= (1 - getA());
        learningAutomataProbability[action] += getA();

        for (int i = 0; i < r; i++) {
            if (i != action) learningAutomataProbability[i] *= (1 - getA());
        }
    }

    // 惩罚动作
    void penalizeAction(int action) {
        learningAutomataProbability[action] *= (1 - getB());

        for (int i = 0; i < r; i++) {
            if (i != action) {
                learningAutomataProbability[i] *= (1 - getB());
                learningAutomataProbability[i] += getB() / (r - 1);
            }
        }
    }

    // 更新学习自动机状态
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

    // 获取学习自动机的概率分布
    public Double[] getLearningAutomataProbability() {
        return learningAutomataProbability;
    }

    // 获取当前动作
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
            // 确保利用率在0和1之间
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

    // 获取MIPS中的利用率差异
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

    // 计算利用率的平均值
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
