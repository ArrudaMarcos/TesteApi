package com.teste.uol.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.teste.uol.api.model.ConsolidatedWeather;
import com.teste.uol.api.model.IpVigilante;
import com.teste.uol.api.model.LocationMetaWeather;

public class Application {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	public static void main(String args[]) {
		String ipAddress = "";
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(ipAddress);
		RestTemplate restTemplate = new RestTemplate();
		IpVigilante ipVigilante = restTemplate.getForObject("https://ipvigilante.com/json/"+ ipAddress, IpVigilante.class);
		log.info(ipVigilante.getData().getLatitude() + "," + ipVigilante.getData().getLongitude());
		LocationMetaWeather[] lmw = restTemplate.getForObject("https://www.metaweather.com/api/location/search/?lattlong="
				+ ipVigilante.getData().getLatitude() + "," + ipVigilante.getData().getLongitude(), LocationMetaWeather[].class);
		log.info(lmw[0].getWoeid());
		
		ConsolidatedWeather cW = restTemplate.getForObject("https://www.metaweather.com/api/location/" + lmw[0].getWoeid() +"/", ConsolidatedWeather.class);
		log.info(cW.getConsolidated_weather()[0].getMax_temp() + "," + cW.getConsolidated_weather()[0].getMin_temp());
	}
}
