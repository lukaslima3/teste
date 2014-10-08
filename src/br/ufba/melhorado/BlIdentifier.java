package br.ufba.melhorado;

import java.io.IOException;

import com.hp.hpl.jena.rdf.model.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.riot.RiotNotFoundException;

/**
 * reads a rdf or n-triple file and checks for broken links.
 * @author patricia
 *
 */
public class BlIdentifier {
	private final String USER_AGENT = "Mozilla/5.0";
	private CountService cs ;
    String url;
	String urlAnterior;
	
	private FileManager fm; // = new FileManager();
	private FileManager fm2;//= new FileManager();

	//HashMap<Code, Tratament> traments;

	public void manageGrafhTest() throws Exception{
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
		cs = cs.getInstance();
		
		fm = new FileManager(cs);
		fm2 = new FileManager(cs);

		try{
			//model.read("dbpedia3.2-persondata_en.nt");
			//model.read("teste1.nt");
			fm.openFile("TesteIdentificacao2");
			//model.read("salvador.rdf");
			model.read("salvadorManipulado2.rdf");

			// list the statements in the Model
			StmtIterator iter = model.listStatements();

			while (iter.hasNext()) {
				Statement stmt      = iter.nextStatement();  // get next statement
				Resource  subject   = stmt.getSubject();     // get the subject
				Property  predicate = stmt.getPredicate();   // get the predicate
				RDFNode   object    = stmt.getObject();      // get the object

				
				if (subject instanceof Resource) {
					url = subject.toString();
					
					if(!url.equals(urlAnterior)){
						urlAnterior=url;
						
						if (cs.getCountTemp502().get() >= 10){
							//sleep 5 minutes
							Thread.sleep(300000);
							cs.getCountTemp502().getAndSet(0);
						}
						
						fm.write("Checking  " + url + "\n"); 
						sendGet(url);
						Thread.sleep(2000);
						//cs.getCount().getAndIncrement();
						cs.increment();
					}
				} 
			}

			fm.showResults();
			fm.closeFile();

		}catch(RiotNotFoundException e1){
			System.out.println("File not found!");
		}
	}

	/**
	 * sends a request to the server and call FileManager class to writes the status on a file.
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
			
			switch (responseCode){
			 case 200:
				//tr.execute();
				//countOk++;
				 cs.getCountOk().getAndIncrement();
				 cs.getCountTemp502().getAndSet(0);
				 break;
			case 301:
				cs.getCountRedirecionado().getAndIncrement();
				fm.write("Status 301 - Recurso redirecionado" + url.toString() + "\n");
				cs.getCountTemp502().getAndSet(0);
				break;
			case 403:
				cs.getCountProibido().getAndIncrement();
				fm.write("Status 403 - Recurso Proibido" + url.toString() + "\n");
				cs.getCountTemp502().getAndSet(0);
				break;
			case 404:
				cs.getCountQueb().getAndIncrement();
				fm.write("Link Quebrado" + url.toString() + "\n");
				cs.getCountTemp502().getAndSet(0);
				break;
			case 405:
				cs.getCountImpedido().getAndIncrement();
				fm.write(" Status 405 - Método não permitido" + url.toString()+ "\n");
				cs.getCountTemp502().getAndSet(0);
				break;
			case 500:
				cs.getCountErroServer().getAndIncrement();
				fm.write(" Status 500 - Erro do Servidor" + url.toString()+ "\n");
				cs.getCountTemp502().getAndSet(0);
				break;
			case 502:
				cs.getCount502().getAndIncrement();
				fm.write(" Status 502 - Bad Gateway" + url.toString()+ "\n");
				
				if (cs.getCount502().get() == 1){
					fm2.openFile("LinksStatus502");
					fm2.write(url.toString()+ "\n");
				}else{
					fm2.write(url.toString()+ "\n");
				}
				break;
			default:
				cs.getCountPesq().getAndIncrement();
				fm.write(" Código diferente é" + responseCode + url.toString()+ "\n");
				break;
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			cs.getCountUnknownHost().getAndIncrement();
		} 
	}

	public static void main(String[] args) throws Exception{
		BlIdentifier rdfm = new BlIdentifier();
		rdfm.manageGrafhTest();
	}
}
