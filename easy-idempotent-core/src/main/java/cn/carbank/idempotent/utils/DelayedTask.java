/*
 * Copyright 2020 chengzhengZhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.carbank.idempotent.utils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Delayed;
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
