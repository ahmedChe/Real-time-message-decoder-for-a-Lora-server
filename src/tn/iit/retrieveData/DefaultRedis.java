package tn.iit.retrieveData;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class DefaultRedis extends Thread {
	String channelDownlink = "smartHome";
	String redisServer = "localhost";
	Jedis jedis;

	public DefaultRedis(String channelDownlink, String redisServer) {

		this.channelDownlink = channelDownlink;
		this.redisServer = redisServer;
		jedis = new Jedis(redisServer);
		jedis.auth("iot2016");
	}

	public void handle(String channel, String message) {
		System.out.println("redis.DefaultRedisClient.handle : " + message);
	}

	public void run() {

		System.out.println("Starting subscriber for channel ");

		while (true) {
			jedis.subscribe(new JedisPubSub() {
				@Override
				public void onMessage(String channel, String message) {

					handle(channel, message);

				}

				@Override
				public void onSubscribe(String channel, int subscribedChannels) {
				}

				@Override
				public void onUnsubscribe(String channel, int subscribedChannels) {
				}

				@Override
				public void onPMessage(String pattern, String channel, String message) {
				}

				@Override
				public void onPUnsubscribe(String pattern, int subscribedChannels) {
				}

				@Override
				public void onPSubscribe(String pattern, int subscribedChannels) {
				}

			}, channelDownlink);
		}

	}

	

}