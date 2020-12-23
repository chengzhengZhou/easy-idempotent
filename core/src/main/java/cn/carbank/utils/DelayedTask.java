package cn.carbank.utils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务
 */
public abstract class DelayedTask implements Runnable, Delayed{
	/**
	 * 当前执行的延迟时间间隔
	 */
	private long delta;
	/**
	 * 延迟执行的时间
	 */
	private long trigger;
	
	/**
	 * 指定下一次的间隔时间的延迟任务
	 * @param delay
	 * @param timeUnit
	 */
	public DelayedTask(long delay, TimeUnit timeUnit) {
		this.delta = timeUnit.toMillis(delay);
		if (this.delta <= 0){
			trigger = System.currentTimeMillis();
		}else{
			trigger = System.currentTimeMillis() + this.delta;
		}
	}

	@Override
	public int compareTo(Delayed o) {
		DelayedTask that = (DelayedTask)o;
		if (this.trigger < that.trigger) {
			return -1;
		} else if (this.trigger > that.trigger) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(trigger - System.currentTimeMillis(), MILLISECONDS);
	}

	@Override
	public String toString () {
		return String.format("[%1$-4d]", delta);
	}
}
