package ltd.finelink.tool.disk.task;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.service.DHTMessageService;
import ltd.finelink.tool.disk.utils.BTUtil;


@Component
@Slf4j
@RequiredArgsConstructor
public class FindNodeTask {

	private ReentrantLock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private BlockingDeque<InetSocketAddress> queue = new LinkedBlockingDeque<>(10240);
	private boolean isRunning;
	private final DHTMessageService dhtMessageService;

	/**
	 * 发送队列
	 */

	/**
	 * 入队首 announce_peer等
	 */
	public void put(InetSocketAddress address) {
		// 如果插入失败
		if (!queue.offer(address)) {
			// 从末尾移除一个
			queue.pollLast();
		}
		if(!isRunning) {
			start();
		}
	}

	/**
	 * 循环执行该任务
	 */
	public void start() {
		isRunning = true;
		// 暂停时长
		int pauseTime = 100;
		TimeUnit milliseconds = TimeUnit.MILLISECONDS;
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				while (true) {
					try {
						dhtMessageService.sendFindNode(queue.take(), BTUtil.generateNodeIdString());
						pause(lock, condition, pauseTime, milliseconds);

					} catch (Exception e) {
						log.error("[FindNodeTask]异常.error:{}", e.getMessage());
					}
				}
			}).start();
		}
	}

	/**
	 * 长度
	 */
	public int size() {
		return queue.size();
	}

	void pause(ReentrantLock lock, Condition condition, long time, TimeUnit timeUnit) {
		if (time <= 0)
			return;
		try {
			lock.lock();
			condition.await(time, timeUnit);
		} catch (Exception e) {
			// ..不可能发生
		} finally {
			lock.unlock();
		}
	}
}
