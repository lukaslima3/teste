import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

public class RDFmanagerTest {
	private static final String personURI = "http://localhost/linkeddata/files/";
	private static final String relationshipUri = "http://purl.org/vocab/relationship/"; // vocabulary
	
	public void manageGrafhTest() throws IOException{
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open("arena.rdf");
		if (in == null) {
		    throw new IllegalArgumentException("File: not found");
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
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }
		    System.out.println(" .");
		}
		
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("InputStream nao contem valor...");
		}
	}
	
	public static void main(String[] args) throws IOException{
		RDFmanagerTest rdfm = new RDFmanagerTest();
		rdfm.manageGrafhTest();
	}

	
}
