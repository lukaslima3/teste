import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;

public class RDFmanagerTest2 {
	private static final String personURI = "http://localhost/linkeddata/files/";
	private static final String relationshipUri = "http://purl.org/vocab/relationship/"; // vocabulary
	private final String USER_AGENT = "Mozilla/5.0";
	String url;
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

		// use the FileManager to find the input file
		//InputStream in = FileManager.get().open("Arena_Fonte_Nova.rdf");
		InputStream in = FileManager.get().open("salvadorManipulado.rdf");
		if (in == null) {
			throw new IllegalArgumentException("Arquivo não encontrado");
		}
		// read the RDF file
		model.read(in, "RDF");

		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  // get next statement
			Resource  subject   = stmt.getSubject();     // get the subject
			Property  predicate = stmt.getPredicate();   // get the predicate
			RDFNode   object    = stmt.getObject();      // get the object

			System.out.print(subject.toString() + " " + predicate.toString() + " ");
			if (object instanceof Resource) {
				url = object.toString();
				//if ( !url.equals("http://dbpedia.org/resource/Salvador,_Bahia")){ 
					sendGet(url);
				//}
				//System.out.print("Objeto é" + object.toString());
				count++;
			} else {
				// object is a literal
				//System.out.print(" \"" + object.toString() + "\"");
			}
			// System.out.println(" .");
		}

		System.out.println("O número de objetos não literais é:" + count);
		System.out.println("O número de links Quebrados é:" + countQueb);
		System.out.println("O número de links 'normais' é:" + countOk);
		System.out.println("O número de links Proibidos é:" + countProibido);
		System.out.println("O número de métodos impedidos é:" + countImpedido);
		System.out.println("O número de redirecionados é:" + countRedirecionado);
		System.out.println("Erro Server:" + countErroServer);
		System.out.println("Unknown Host Name:" + countUnknownHost);
		System.out.println("O número resposta diferente de 4.. e 200 é:" + countPesq);

		try {

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("InputStream nao contem valor...");
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
					countImpedido++;
					System.out.println(" Status 500 - Erro do Servidor" + url.toString());
					break;
				default:
					//System.out.println("Pesquisar Codigo");
					countErroServer++;
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
	
	public static void main(String[] args) throws Exception{
		RDFmanagerTest2 rdfm = new RDFmanagerTest2();
		rdfm.manageGrafhTest();

	}
}
