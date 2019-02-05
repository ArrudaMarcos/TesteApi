 package com.teste.uol.api.service;



import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.teste.uol.api.model.ConsolidatedWeather;
import com.teste.uol.api.model.Historico;
import com.teste.uol.api.model.IpVigilante;
import com.teste.uol.api.model.LocationMetaWeather;

@Service
public class ConfigService {

	

	@Autowired
	private HttpServletRequest request;
	
	
	public Historico preencherHistorico() {
		
		Historico historico = new Historico();
		
		String ipAddress = obterIP();
		
		consomeIpVigilante(historico, ipAddress);
		consomeMetaweather(historico);
		
		return historico;
		
	}	
	
	
	private String obterIP() {
		
		String ipAddress = request.getHeader("x-forwarded-for");
		
		if (ipAddress == null) {
			ipAddress = request.getHeader("X_FORWARDED_FOR");
			if (ipAddress == null){
				ipAddress = request.getRemoteAddr();
			}
		}
		return ipAddress;
		
	}
	
	private void consomeIpVigilante(Historico historico, String ipAddress) {
				
		RestTemplate restTemplate = new RestTemplate();
		
		String url = "https://ipvigilante.com/json/" + ipAddress;
		IpVigilante ipVigilante = restTemplate.getForObject(url, IpVigilante.class);
		
		historico.setLatitude(ipVigilante.getData().getLatitude());
		historico.setLongitude(ipVigilante.getData().getLongitude());		
	
	}
	
	private void consomeMetaweather(Historico historico){
		
		RestTemplate restTemplate = new RestTemplate();
		
		String url = "https://www.metaweather.com/api/location/search/?lattlong="
				+ historico.getLatitude() + "," + historico.getLongitude();
		
		LocationMetaWeather[] lmw = restTemplate.getForObject(url, LocationMetaWeather[].class);

		pegaTemperatura(lmw[0].getWoeid(), historico);
		
	}

	private void pegaTemperatura(String woeid, Historico historico) {
		
		String TemperaturaMax = "";
		String TemperaturaMin = "";
		
		RestTemplate restTemplate = new RestTemplate();		
		
		String url = "https://www.metaweather.com/api/location/" + woeid;
		ConsolidatedWeather cW = restTemplate.getForObject(url, ConsolidatedWeather.class);
		
		//Formatando String
		TemperaturaMax = cW.getConsolidated_weather()[0].getMax_temp().substring(0, 4);
		TemperaturaMin = cW.getConsolidated_weather()[0].getMin_temp().substring(0, 4);
		
		//Inserindo em historico
		historico.setTemperaturaMax(TemperaturaMax);
		historico.setTemperaturaMin(TemperaturaMin);
	}

	
}


