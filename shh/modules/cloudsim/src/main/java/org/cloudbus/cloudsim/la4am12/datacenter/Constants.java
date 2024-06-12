package org.cloudbus.cloudsim.la4am12.datacenter;

/**
 * @author : LA4AM12
 * @create : 2023-03-15 09:43:57
 * @description :
 */
public interface Constants {
	/**
	 * 低性能 mips
	 */
	public static final int L_MIPS = 1000;

	/**
	 * 中等性能 mips
	 */
	public static final int M_MIPS = 3000;

	/**
	 * 高性能 mips
	 */
	public static final int H_MIPS = 5000;

	/**
	 * 低性能价格（每秒美元）
	 */
	public static final double L_PRICE = 0.3;

	/**
	 * 中等性能价格（每秒美元）
	 */
	public static final double M_PRICE = 0.5;

	/**
	 * 高性能价格（每秒美元）
	 */
	public static final double H_PRICE = 0.9;

	/**
	 * 低性能虚拟机数量
	 */
	public static final int L_VM_N = 4;//8/16

	/**
	 * 中等性能虚拟机数量
	 */
	public static final int M_VM_N = 4;//8/16

	/**
	 * 高性能虚拟机数量
	 */
	public static final int H_VM_N = 2;//4/8

	/**
	 * 每个虚拟机的内存(MB)
	 */
	public static final int RAM = 2048;

	/**
	 * 虚拟机存储容量
	 */
	public static final long STORAGE = 100000;

	/**
	 * 虚拟机镜像大小
	 */
	public static final long IMAGE_SIZE = 10000;

	/**
	 * 虚拟机带宽
	 */
	public static final int BW = 1024;
}
