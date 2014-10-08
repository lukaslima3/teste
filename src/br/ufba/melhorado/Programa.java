package br.ufba.melhorado;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.entity.UrlEncodedFormEntity;

public class Programa implements Runnable {
	
	
	private final String USER_AGENT = "Mozilla/5.0";
	String url;
	String urlAnterior;
	int ativas=0;
	public int countQueb=0;
	public int countOk=0;
	public int countPesq=0;
	public int countProibido=0;
	public int countImpedido=0;
	public int countRedirecionado=0;
	public int countErroServer=0;
	public int countUnknownHost=0;
	public int badGateway=0;
	public ArrayList<String> urlDesconhecidas = new ArrayList<String> ();
	public ArrayList<String> urlBroken = new ArrayList<String> ();
	public ArrayList<String> urlErrorServer = new ArrayList<String> ();
	public ArrayList<String> urlBadGateway = new ArrayList<String> ();
	
	public void setUrl(String url) {
		this.url=url;
		
	}
	
	public void run() {
		URL obj = null;
		HttpURLConnection con = null;
		int responseCode;
		String urlUtf;
		urlUtf=url;
		try {
			ativas++;
			
			if(url.indexOf("%2")!=-1){
				url=URLDecoder.decode(url, "UTF-8");
				
			}
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			//String t="http://th.dbpedia.org/resource/ซัลวาดอร์";
			con.setRequestMethod("GET");

			con.setRequestProperty("User-Agent", USER_AGENT);
			responseCode = con.getResponseCode();
			con.disconnect();
	
			
			switch (responseCode){
			case 200:
				countOk++;
				break;
			case 301:
				countRedirecionado++;
				//System.out.println("Status 301 - Recurso redirecionado" + url.toString());
				break;
			case 403:
				countProibido++;
				System.out.println("Status 403 - Recurso Proibido" + url.toString());
				break;
			case 404:
				if(tryAgain(urlUtf.toString(),0)==0){
					System.out.println("Link Quebrado" + urlUtf.toString());
				}
				break;
			case 405:
				countImpedido++;
				System.out.println(" Status 405 - MÃ©todo nÃ£o permitido" + url.toString());
				break;
			case 500:
				tryAgain(urlUtf.toString(),1);
				break;
			case 502:
				urlBadGateway.add(urlUtf.toString());
				badGateway++;
				break;
			default:
				countPesq++;
				System.out.println(" CÃ³digo diferente Ã©" + responseCode + url.toString());
				break;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) { // erro DNS
			//como fazer um tratamento especÃ­fico para Class UnknownHostException que Ã© do tipo IO?
			
			//e1.printStackTrace();
			urlDesconhecidas.add(urlUtf.toString());
			//System.out.println(" Url referida: "  + url.toString());
			countUnknownHost++;
		}finally {
			ativas--;
		} 
		
	}

	private int tryAgain(String string,int flag) {
		URL obj = null;
		HttpURLConnection con = null;
		int responseCode;
		
		String urlUtf=string;
		try {
			
			System.out.println("erra assim"+urlUtf);
			if(flag==0)
			urlUtf=URLDecoder.decode(urlUtf, "UTF-8");
			
			obj = new URL(urlUtf);
			con = (HttpURLConnection) obj.openConnection();

			
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			responseCode = con.getResponseCode();
			con.disconnect();
			
			switch (responseCode){
			case 200:
				countOk++;
				return 1;
			case 301:
				countRedirecionado++;
				System.out.println("Status 301 - Recurso redirecionado" + url.toString());
				return 0;
			case 403:
				countProibido++;
				return 0;
			case 404:
				urlBroken.add(urlUtf.toString());
				countQueb++;
				return 0;
			case 405:
				countImpedido++;
				return 0;
			case 500:
				urlErrorServer.add(urlUtf.toString());
				countErroServer++;
				return 0;
			case 502:
				urlBadGateway.add(urlUtf.toString());
				badGateway++;
				return 0;
			default:
				countPesq++;
				return 0;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			urlDesconhecidas.add(string.toString()); 
			countUnknownHost++;
			return 0;
		}finally {
			ativas--;
		} 
		return 1;
	}

		   

	
	 }