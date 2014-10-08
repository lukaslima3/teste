package br.ufba.melhorado;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import br.ufba.melhorado.CountService;

/**
 * responsable for any operation of writing on a result file. It uses the singleton pattern.
 * @author patricia
 *
 */



public class FileManager implements Observer {
	
	private static FileManager uniqueInstance;
	private BufferedWriter out;
	

	private FileInputStream arquivo ;
	private InputStreamReader reader;// = new InputStreamReader(arquivo);
	private BufferedReader buffer;// = new BufferedReader(reader);

	
	

	Observable countService;

	FileManager(Observable countService) {
		
		this.countService = countService;
		countService.addObserver(this);
	}


	
	/**
	 * opens the file and writes test's initial date and time
	 */
	void openFile(String fileName){
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()); 
			//out.write("Início dos testes " + dateTime + "\n" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void closeFile() throws IOException{
		out.close();
	}
	
	void write(String msg){
		try {
			out.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//divide o arquivo e retorna o numero de arquivos
	int readOnLine(String archiveName) throws IOException{
		arquivo = new FileInputStream(archiveName);
		reader = new InputStreamReader(arquivo);
		buffer = new BufferedReader(reader);
		String linha = buffer.readLine();
		
		while(linha != null) {
			   
		   linha = buffer.readLine();
		   System.out.println(linha);
			}
		
		return 2;
	}
	
	
	/**
	 * write the final results on a file
	 * @throws IOException 
	 * 
	 */
	void showResults() throws IOException{
		CountService cs = CountService.getInstance();
		
		out.write("/------------------------Resultado Final------------------------/" + "\n" );
		out.write("O número total de links pesquisados é:" + cs.getCount() + "\n");
		out.write("O número de links Quebrados é:" + cs.getCountQueb() + "\n");
		out.write("O número de links ok é:" + cs.getCountOk() + "\n");
		out.write("O número de links Proibidos é:" + cs.getCountProibido()+ "\n");
		out.write("O número de métodos impedidos é:" + cs.getCountImpedido()+ "\n");
		out.write("O número de redirecionados é:" + cs.getCountRedirecionado()+ "\n");
		out.write("Erro Server:" + cs.getCountErroServer()+ "\n");
		out.write("Unknown Host Name:" + cs.getCountUnknownHost()+ "\n");
		out.write("Bad Gatway:" + cs.getCount502()+ "\n");
		out.write("O número resposta diferente de 4.. e 200 é:" + cs.getCountPesq() + "\n");
		out.write("/------------------------------------------------------------------/" + "\n");
		String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());  
		out.write("Fim dos testes: " + dateTime + "\n");
		System.out.println("Teste finalizado");
	}



	@Override
	public void update(Observable contadorSubject, Object arg1) {

		if (contadorSubject instanceof CountService) {
			CountService cs = CountService.getInstance();

			System.out.println("/------------------------Resultado Parcial------------------------/" + "\n" );
			System.out.println("O número total de links pesquisados é:" + cs.getCount() + "\n");
			System.out.println("O número de links ok é:" + cs.getCountOk() + "\n");
			System.out.println("O número de links Quebrados é:" + cs.getCountQueb() + "\n");
			System.out.println("O número de links Proibidos é:" + cs.getCountProibido()+ "\n");
			System.out.println("O número de métodos impedidos é:" + cs.getCountImpedido()+ "\n");
			System.out.println("O número de redirecionados é:" + cs.getCountRedirecionado()+ "\n");
			System.out.println("Erro Server:" + cs.getCountErroServer()+ "\n");
			System.out.println("Unknown Host Name:" + cs.getCountUnknownHost()+ "\n");
			System.out.println("Bad Gatway:" + cs.getCount502()+ "\n");
			System.out.println("O número resposta diferente de 4.. e 200 é:" + cs.getCountPesq() + "\n");
			System.out.println("/------------------------------------------------------------------/" + "\n");
			String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());  
			System.out.println( dateTime + "\n");

		} 
	}
		
	
	public BufferedWriter getOut() {
		return out;
	}

	public void setOut(BufferedWriter out) {
		this.out = out;
	}

	public Observable getCountService() {
		return countService;
	}

	public void setCountService(Observable countService) {
		this.countService = countService;
	}
}
