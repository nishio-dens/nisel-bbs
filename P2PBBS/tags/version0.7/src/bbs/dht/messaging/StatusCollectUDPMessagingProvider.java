/*
 * Copyright 2006-2008 National Institute of Advanced Industrial Science
 * and Technology (AIST), and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bbs.dht.messaging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ow.messaging.AbstractMessagingProvider;
import ow.messaging.InetMessagingAddress;
import ow.messaging.MessageReceiver;
import ow.messaging.MessagingAddress;
import ow.messaging.MessagingConfiguration;
import ow.messaging.MessagingProvider;
import ow.messaging.timeoutcalc.RTTBasedTimeoutCalculator;
import ow.messaging.timeoutcalc.StaticTimeoutCalculator;
import ow.messaging.timeoutcalc.TimeoutCalculator;
import ow.messaging.udp.UDPMessageReceiver;
import ow.messaging.udp.UDPMessagingConfiguration;
import ow.messaging.udp.UDPMessagingProvider;

//ow.messaging.MessagingFactoryの一部をStatusCollectUDPMessagingProviderへと変更
/**
 * メッセージサイズの収集の際に利用
 * @author nishio
 *
 */
public class StatusCollectUDPMessagingProvider extends AbstractMessagingProvider {
	private final static String NAME = "UDP";

	private final Map<Integer,StatusCollectUDPMessageReceiver> receiverTable =
		new HashMap<Integer,StatusCollectUDPMessageReceiver>();

	private InetAddress selfInetAddress = null;
	private TimeoutCalculator timeoutCalculator = null;
	private MessagingAddress statCollectorAddress = null;

	public String getName() { return NAME; }
	public boolean isReliable() { return false; }

	public MessagingConfiguration getDefaultConfiguration() { return new UDPMessagingConfiguration(); } 

	public MessageReceiver getReceiver(MessagingConfiguration config, int port) throws IOException {
		return getReceiver(config, port, 1);
	}

	public MessageReceiver getReceiver(MessagingConfiguration config, int port, int portRange) throws IOException {
		synchronized (this) {
			if (this.timeoutCalculator == null) {
				if (config.getDoTimeoutCalculation()) {
					this.timeoutCalculator = new RTTBasedTimeoutCalculator(config);
				}
				else {
					this.timeoutCalculator = new StaticTimeoutCalculator(config);
				}
			}
		}

		StatusCollectUDPMessageReceiver receiver;

		synchronized (this.receiverTable) {
			receiver = this.receiverTable.get(port);

			if (receiver == null) {
				receiver = new StatusCollectUDPMessageReceiver(this.selfInetAddress, port, portRange,
						(UDPMessagingConfiguration)config, this);
				receiverTable.put(receiver.getSelfAddress().getPort(), receiver);
				receiver.start();
			}
		}

		return receiver;
	}

	public InetMessagingAddress getMessagingAddress(String hostname, int port)
			throws UnknownHostException {
		return new InetMessagingAddress(hostname, port);
	}

	public InetMessagingAddress getMessagingAddress(String hostAndPort)
			throws UnknownHostException {
		return new InetMessagingAddress(hostAndPort);
	}

	protected InetMessagingAddress getMessagingAddress(InetSocketAddress inetSockAddress) {
		return new InetMessagingAddress(inetSockAddress);
	}

	public InetMessagingAddress getMessagingAddress(int port) {
		return new InetMessagingAddress(port);
	}

	public TimeoutCalculator getTimeoutCalculator() { return this.timeoutCalculator; }

	public MessagingAddress getMessagingCollectorAddress() { return this.statCollectorAddress; }
	public MessagingAddress setMessagingCollectorAddress(MessagingAddress addr) {
		MessagingAddress old;

		synchronized (this) {
			old = this.statCollectorAddress;
			this.statCollectorAddress = addr;
		}

		return old;
	}

	public MessagingProvider substitute() {
		return null;
	}

	public void setSelfAddress(String host) throws UnknownHostException {
		this.selfInetAddress = InetAddress.getByName(host);

		synchronized (this.receiverTable) {
			for (StatusCollectUDPMessageReceiver receiver: this.receiverTable.values()) {
				receiver.setSelfAddress(host);
			}
		}
	}
}
