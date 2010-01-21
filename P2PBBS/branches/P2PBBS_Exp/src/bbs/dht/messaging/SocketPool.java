package bbs.dht.messaging;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A pool of {@link java.nio.channels.DatagramChannel DatagramChannel} instances.
 * {@link ow.messaging.udp.UDPMessageSender UDPMessageSender} gets an instance
 * for each context to avoid cross-talk.
 */
final class SocketPool {
	private final static Logger logger = Logger.getLogger("bbs.messaging");

	private int capacity;
	private Stack<DatagramChannel> sockStack;

	SocketPool(int capacity) {
		this.capacity = capacity;
		this.sockStack = new Stack<DatagramChannel>();
	}

	public DatagramChannel get() {
		DatagramChannel sock = null;

		synchronized (this.sockStack) {
			try {
				sock = this.sockStack.pop();
			}
			catch (EmptyStackException e) {
				try {
					sock = DatagramChannel.open();
				}
				catch (IOException e0) {
					// NOTREACHED
					logger.log(Level.WARNING, "Cound not instantiate a DatagramSocket.");
				}
			}
		}

		return sock;
	}

	public void put(DatagramChannel sock) {
		synchronized (this.sockStack) {
			if (this.sockStack.size() < this.capacity) {
				this.sockStack.push(sock);
			}
		}
	}
}
