import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Programa implements Runnable {
	
	
	private final String USER_AGENT = "Mozilla/5.0";
	String url;
	String urlAnterior;
	int count=0;
	int id=0;
	int countQueb=0;
	int countOk=0;
	int countPesq=0;
	int countProibido=0;
	int countImpedido=0;
	int countRedirecionado=0;
	int countErroServer=0;
	int countUnknownHost=0;
	
	public void setUrl(String url) {
		this.url=url;
		
	}
	public void setId(int id) {
		this.id=id;
		
	}
	
	public void run() {

		URL obj = null;
		HttpURLConnection con = null;
		int responseCode;

		try {
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			responseCode = con.getResponseCode();
			//System.out.println("\nSending 'GET' request to URL : " + url);
			//System.out.println("Response Code : " + responseCode);

			switch (responseCode){
			case 200:
				countOk++;
				break;
			case 301:
				countRedirecionado++;
				System.out.println("Status 301 - Recurso redirecionado" + url.toString());
				break;
			case 403:
				countProibido++;
				System.out.println("Status 403 - Recurso Proibido" + url.toString());
				break;
			case 404:
				countQueb++;
				System.out.println("Link Quebrado" + url.toString());
				break;
			case 405:
				countImpedido++;
				System.out.println(" Status 405 - Método não permitido" + url.toString());
				break;
			case 500:
				countErroServer++;
				System.out.println(" Status 500 - Erro do Servidor" + url.toString());
				break;
			default:
				//System.out.println("Pesquisar Codigo");
				countPesq++;
				System.out.println(" Código diferente é" + responseCode + url.toString());
				break;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			//como fazer um tratamento específico para Class UnknownHostException que é do tipo IO?
			//e1.printStackTrace();
			countUnknownHost++;

		} 
	}

		   

	
	 }