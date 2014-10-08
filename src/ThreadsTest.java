
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.riot.RiotNotFoundException;


public class ThreadsTest {
//	private final String USER_AGENT = "Mozilla/5.0";
	String url;
	String urlAnterior;
	int count=0;
//	int countQueb=0;
//	int countOk=0;
//	int countPesq=0;
//	int countProibido=0;
//	int countImpedido=0;
//	int countRedirecionado=0;
//	int countErroServer=0;
//	int countUnknownHost=0;

	public void manageGrafhTest() throws Exception{
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		try{
			model.read("dbpedia3.2-persondata_en.nt");

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
						
						  Programa p1 = new Programa();  
						     p1.setUrl(url);
						     
						     Thread t1 = new Thread(p1);
						     t1.start();
						     System.out.println("sssss1 Checando  " + url);
//						     
//						     iter.hasNext();
//						     stmt      = iter.nextStatement();  // get next statement
//							subject   = stmt.getSubject();     // get the subject
//							predicate = stmt.getPredicate();   // get the predicate
//							object    = stmt.getObject();      // get the object
//						     Programa p2 = new Programa();  
//						     p1.setUrl(url);
//						     
//						     Thread t2 = new Thread(p1);
//						     t2.start();
//						     System.out.println("2 Checando  " + url);

						count++;
					}
				} 
			}

//			apresentaResultado();

		}catch(RiotNotFoundException e1){
			System.out.println("Arquivo não encontrado!");
		}
	}



//	void apresentaResultado(){
//		System.out.println("O número de pessoas distintas é:" + count);
//		System.out.println("O número de links Quebrados é:" + countQueb);
//		System.out.println("O número de links 'normais' é:" + countOk);
//		System.out.println("O número de links Proibidos é:" + countProibido);
//		System.out.println("O número de métodos impedidos é:" + countImpedido);
//		System.out.println("O número de redirecionados é:" + countRedirecionado);
//		System.out.println("Erro Server:" + countErroServer);
//		System.out.println("Unknown Host Name:" + countUnknownHost);
//		System.out.println("O número resposta diferente de 4.. e 200 é:" + countPesq);
//	}

	public static void main(String[] args) throws Exception{
		NtriplemanagerTest rdfm = new NtriplemanagerTest();
		rdfm.manageGrafhTest();

	}
}

