import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import com.hp.hpl.jena.rdf.model.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.riot.RiotNotFoundException;

/**
 * reads a rdf or n-triple file and checks for broken links.
 * @author patricia
 *
 */
public class NtriplemanagerTestObj {
	private final String USER_AGENT = "Mozilla/5.0";
    String url;
	String urlAnterior;
	BufferedWriter out;
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
			model.read("simples2.rdf");
			openFile();

			// list the statements in the Model
			StmtIterator iter = model.listStatements();

			while (iter.hasNext()) {
				Statement stmt      = iter.nextStatement();  // get next statement
				Resource  subject   = stmt.getSubject();     // get the subject
				Property  predicate = stmt.getPredicate();   // get the predicate
				RDFNode   object    = stmt.getObject();      // get the object

				//System.out.print(subject.toString() + " " + predicate.toString() + " ");
				if (object instanceof Resource) {
					url = object.toString();
					
					if(!url.equals(urlAnterior)){
						urlAnterior=url;
						out.write("Checking  " + url + "\n"); 
						sendGet(url);
						Thread.sleep(3000);  
						count++;
					}
				} 
			}

			showResults();
			out.close();

		}catch(RiotNotFoundException e1){
			System.out.println("File not found!");
		}
	}

	/**
	 * sends a request to the server and writes the status on a file.
	 * @param url
	 */
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
				countOk++;
				break;
			case 301:
				countRedirecionado++;
				//System.out.println("Status 301 - Recurso redirecionado" + url.toString());
				out.write("Status 301 - Recurso redirecionado" + url.toString() + "\n");
				break;
			case 403:
				countProibido++;
				out.write("Status 403 - Recurso Proibido" + url.toString() + "\n");
				break;
			case 404:
				countQueb++;
				out.write("Link Quebrado" + url.toString() + "\n");
				break;
			case 405:
				countImpedido++;
				out.write(" Status 405 - Método não permitido" + url.toString()+ "\n");
				break;
			case 500:
				countImpedido++;
				out.write(" Status 500 - Erro do Servidor" + url.toString()+ "\n");
				break;
			default:
				//System.out.println("Pesquisar Codigo");
				countErroServer++;
				out.write(" Código diferente é" + responseCode + url.toString()+ "\n");
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

	
	/**
	 * write the final results on a file
	 * @throws IOException 
	 * 
	 */
	void showResults() throws IOException{
		out.write("/------------------------Resultados Finais ------------------------/" + "\n" );
		out.write("O número de pessoas distintas é:" + count + "\n");
		out.write("O número de links Quebrados é:" + countQueb + "\n");
		out.write("O número de links 'normais' é:" + countOk + "\n");
		out.write("O número de links Proibidos é:" + countProibido+ "\n");
		out.write("O número de métodos impedidos é:" + countImpedido+ "\n");
		out.write("O número de redirecionados é:" + countRedirecionado+ "\n");
		out.write("Erro Server:" + countErroServer+ "\n");
		out.write("Unknown Host Name:" + countUnknownHost+ "\n");
		out.write("O número resposta diferente de 4.. e 200 é:" + countPesq + "\n");
		out.write("/------------------------------------------------------------------/" + "\n");
		String data = new SimpleDateFormat("dd/MM/yyyy").format(new Date());  
		String hora = new SimpleDateFormat("HH:mm").format(new Date());
		out.write("Fim dos testes:" + "Dia  " + data + "  às  " + hora + "\n");
		System.out.println("Teste finalizado");
	}
	
	/**
	 * opens the file and writes test's initial date and time
	 */
	void openFile(){
		try {
			out = new BufferedWriter(new FileWriter("TesteIdentificacao"));
			String data = new SimpleDateFormat("dd/MM/yyyy").format(new Date());  
			String hora = new SimpleDateFormat("HH:mm").format(new Date());
			out.write("Início dos testes:" + "Dia  " + data + "  às  " + hora + "\n\n" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) throws Exception{
		NtriplemanagerTestObj rdfm = new NtriplemanagerTestObj();
		rdfm.manageGrafhTest();
	}
}
