package tn.iit.retrieveData;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import tn.iit.Decrypter.LoraDecrypter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
public class RetrieveDATA extends DefaultRedis {
	LoraDecrypter decrypter;
	private int cpt=0;
	public RetrieveDATA(String channelDownlink, String redisServer) {
		super(channelDownlink, redisServer);

	}
	
	@Override
	public void handle(String channel, String message) {
		Object object = null;
		super.handle(channel, message);
		byte[] bytes = DatatypeConverter.parseHexBinary(message);
		try {
			byte [] subArray = Arrays.copyOfRange(bytes, 12, bytes.length);
			String messages=new String(subArray, "UTF-8");
			if (messages.contains("rxpk"))
			{
				JSONParser jsonParser=new JSONParser();
				object = jsonParser.parse(messages);
				JSONArray  array=new JSONArray();
				array.add(object);
				JSONObject objet = (JSONObject)array.get(0);
				JSONArray objetDesire=(JSONArray)objet.get("rxpk");
				JSONObject msg= (JSONObject)objetDesire.get(0);
				String data=msg.get("data").toString();
				decrypter = new LoraDecrypter(
						Hex.decode("App Key"));
				byte[] decrypt = decrypter.decrypt(data);
				byte [] subDecrypt = Arrays.copyOfRange(decrypt,0, (decrypt.length)-4);
				String affiche=new String(subDecrypt, Charset.forName("UTF-8"));
				System.out.println(affiche);
			}
		} catch (UnsupportedEncodingException | ParseException e) {
			e.printStackTrace();
		}		
	}

	public static void main(String[] args) {
		RetrieveDATA receiveRedisFrame = new RetrieveDATA("lora", "@ du serveur");
		receiveRedisFrame.start();

	}

}
