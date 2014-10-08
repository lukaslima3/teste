import java.io.IOException;

import com.hp.hpl.jena.rdf.model.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.riot.RiotNotFoundException;

public class NtriplemanagerTest {
	private final String USER_AGENT = "Mozilla/5.0";
	String url;
	String urlAnterior;
	int count=0;
	int countQueb=0;
	int countOk=0;
	int countPesq=0;
	int countProibido=0;
	int countImpedido=0;
	int countRedirecionado=0;
	int countErroServer=0;
	int countUnknownHost=0;

	public void manageGrafhTest() throws Exception{
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		try{
			//model.read("George_W._Bush.rdf");
//			model.read("salvadorManipulado.rdf");
			//model.read("Arena_Fonte_nova.rdf");
			model.read("salvador.rdf");
			// list the statements in the Model
			StmtIterator iter = model.listStatements();

			while (iter.hasNext()) {
				Statement stmt      = iter.nextStatement();  // get next statement
				Resource  subject   = stmt.getSubject();     // get the subject
				Property  predicate = stmt.getPredicate();   // get the predicate
				RDFNode   object    = stmt.getObject();      // get the object

				//System.out.print(subject.toString() + " " + predicate.toString() + " ");
				if (subject instanceof Resource) {
					url = subject.toString();
					
					if(!url.equals(urlAnterior)){
						urlAnterior=url;
						System.out.println("Checando  " + url); 
						sendGet(url);
						count++;
					}
				} 
			}

			apresentaResultado();

		}catch(RiotNotFoundException e1){
			System.out.println("Arquivo não encontrado!");
		}
	}

	// HTTP GET request
	private void sendGet(String url) {

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
				System.out.println(" Url referida: "  + url.toString());
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
			System.out.println(" Url referida: "  + url.toString());
			countUnknownHost++;

		} 
	}

	void apresentaResultado(){
		System.out.println("O número de pessoas distintas é:" + count);
		System.out.println("O número de links Quebrados é:" + countQueb);
		System.out.println("O número de links 'normais' é:" + countOk);
		System.out.println("O número de links Proibidos é:" + countProibido);
		System.out.println("O número de métodos impedidos é:" + countImpedido);
		System.out.println("O número de redirecionados é:" + countRedirecionado);
		System.out.println("Erro Server:" + countErroServer);
		System.out.println("Unknown Host Name:" + countUnknownHost);
		System.out.println("O número resposta diferente de 4.. e 200 é:" + countPesq);
	}

	public static void main(String[] args) throws Exception{
		
		long antes = System.currentTimeMillis()/1000; 
		NtriplemanagerTest rdfm = new NtriplemanagerTest();
		rdfm.manageGrafhTest();
		
		long tempo = System.currentTimeMillis()/1000 - antes;  
	      //O tempo total decorrido � mostrado aqui  
	      System.out.printf("O programa executou em %d Segundos.%n", tempo);


	}
}
