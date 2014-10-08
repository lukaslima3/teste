package br.ufba.melhorado;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;










import java.io.InputStreamReader;

import com.hp.hpl.jena.rdf.model.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.jena.riot.RiotNotFoundException;


public class ThreadsTest {
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
	int badGateway=0;
	//String nomeArquivo="dbpedia3.2-persondata_en.nt";//"salvador.rdf";
	//String nomeArquivo="dbpedia3.2-persondata_en.nt";
	//String nomeArquivo="George_W._Bush.rdf";
	String nomeArquivo="salvadorManipulado2.rdf";
	//String nomeArquivo="testePage.nt";
	//String nomeArquivo="persondata_en 3.6.nt";//manipulado teste
	//String nomeArquivo="manipulado teste.nt";//
	//String nomeArquivo="testeErro2.nt";//
	//String nomeArquivo="teste.nt";
	
	private CountService cs ;
	private FileManager fm,errosDesconhecidos,brokenLinks,urlErrorServer,urlBadGateway;
	
	public void setAtributes(Programa[] p,Programa[] p2, int Nthreads){
		
		for(int i=0;i<Nthreads;i++){
			this.countOk+=p[i].countOk+p2[i].countOk;
			this.countPesq+=p[i].countPesq+p2[i].countPesq;
			this.countProibido+=p[i].countProibido;
			this.countImpedido+=p[i].countImpedido+p2[i].countProibido;
			this.countRedirecionado+=p[i].countRedirecionado+p2[i].countRedirecionado;
			this.countQueb+=p2[i].urlBroken.size();
			this.countErroServer+=p2[i].urlErrorServer.size();
			this.countUnknownHost+=p2[i].urlDesconhecidas.size();
			this.badGateway+=p2[i].urlBadGateway.size();
		}
	}
	
	
	
	

	public void manageGrafhTest() throws Exception{
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
		try{
	
			model.read(nomeArquivo);
			
			int nThreads=15;
			cs = cs.getInstance();
			fm = new FileManager(cs);
			boolean open=false;
			StmtIterator iter = model.listStatements();
			Thread t1[]=new Thread[nThreads];
			Programa p1[] = new Programa[nThreads];
			inicializaPrograma(p1,t1,nThreads);
			
			
			int indice=0;
			while (iter.hasNext()) {
				Statement stmt      = iter.nextStatement();  // get next statement
				Resource  subject   = stmt.getSubject();     // get the subject
				//System.out.println(stmt.toString());
				if (subject instanceof Resource) {
					url = subject.toString();
					String urlPura=url;
					url=url.replaceAll("%28", "(");
					url=url.replaceAll("%29", ")");
					if(!url.equals(urlAnterior)){
						
						urlAnterior=url;
						 //System.out.println("Checando  " + url);
						//System.out.println("Linguagem  " + URLDecoder.decode(url, "UTF-8"));
						// System.out.println("Checando  " + );
						if(!open){//tem erro aqui TODO
							fm.openFile("Lista de links em "+nomeArquivo);
						fm.write(URLDecoder.decode(urlPura, "UTF-8")+ "\n");
						open=true;
							}
						else
							fm.write(URLDecoder.decode(urlPura, "UTF-8")+ "\n");
						
						     p1[indice].setUrl(url);
						     t1[indice] = new Thread(p1[indice]);
						     count++;
						     t1[indice].start();
						    
						    	 indice=waitOneThreadStop(t1,nThreads);
					}
				}
				
				
			}

			
			waitAllThreadStoped(t1,nThreads);
			//Thread.sleep(150000);
			Programa p2[] = tryAgain(p1,nThreads);
		     setAtributes(p1,p2,nThreads);//altera valores de links quebrados
			apresentaResultado();
			saveErrorListInarchive(p1,p2,nThreads);
			fm.closeFile();
		}catch(RiotNotFoundException e1){
			System.out.println("Arquivo nÃ£o encontrado!");
		}
	}





/**
 * 
 * @param p1
 * @param p2
 * @param nThreads
 * @throws IOException
 * Salva cada Links em seu respectivo Arquivo
 */
	private void saveErrorListInarchive(Programa[] p1, Programa[] p2, int nThreads) throws IOException {
		
		
		cs = cs.getInstance();
		errosDesconhecidos=new FileManager(cs);
		brokenLinks=new FileManager(cs);
		urlErrorServer=new FileManager(cs);
		urlBadGateway=new FileManager(cs);
		errosDesconhecidos.openFile("Erros em "+nomeArquivo);
		brokenLinks.openFile("Quebrados em "+nomeArquivo);
		urlErrorServer.openFile("ErroServer "+nomeArquivo);
		urlBadGateway.openFile("BadGateway "+nomeArquivo);
		for(int i=0;i<nThreads;i++){
			if(p1[i].urlDesconhecidas.size()>0){
				for(int a=0;a<p1[i].urlDesconhecidas.size();a++){  
					errosDesconhecidos.write(p1[i].urlDesconhecidas.get(a).toString()+ "\n");    
					}  
			}
			if(p2[i].urlBroken.size()>0){
				for(int a=0;a<p2[i].urlBroken.size();a++){ 
					brokenLinks.write(p2[i].urlBroken.get(a).toString());//+">"+"<"+p2[i].urlBroken.get(a).toString()+">"+"<"+p2[i].urlBroken.get(a).toString()+">."+"\n");    
					}
			}
			if(p1[i].urlErrorServer.size()>0){
				for(int a=0;a<p1[i].urlErrorServer.size();a++){  
					urlErrorServer.write(p1[i].urlErrorServer.get(a).toString()+ "\n");    
					}
			}
			if(p2[i].urlBadGateway.size()>0){
				for(int a=0;a<p2[i].urlBadGateway.size();a++){  
					urlBadGateway.write(p2[i].urlBadGateway.get(a).toString()+"\n");    
					}
			}
			
			
			
			
			//public ArrayList<String> urlErrorServer = new ArrayList<String> ();
			//public ArrayList<String> urlBadGateway = new ArrayList<String> ();
		}
			//System.out.println("teste "+p1[i].urlDesconhecidas.toString());
		
		errosDesconhecidos.closeFile();
		brokenLinks.closeFile();
		urlErrorServer.closeFile();
		urlBadGateway.closeFile();
		
	}
	
	/**
	 * 
	 * @param p1
	 * @param nThreads
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * Tenta Novamente os links onde ouveram erro 404 e 502
	 */
	private Programa[] tryAgain(Programa[] p1, int nThreads) throws IOException, InterruptedException {
	
		Thread t1[]=new Thread[nThreads];
		Programa retry[] = new Programa[nThreads];
		inicializaPrograma(retry,t1,nThreads);
		int indice=0;
		for(int i=0;i<nThreads;i++){
			System.out.println("tentar denovo");
			if(p1[i].urlBroken.size()>0){
				for(int a=0;a<p1[i].urlBroken.size();a++){ 
					retry[indice].setUrl(p1[i].urlBroken.get(a).toString());
					t1[indice] = new Thread(retry[indice]);
					t1[indice].start();
					indice=waitOneThreadStop(t1,nThreads);    
					}
			}
			
			if(p1[i].urlBadGateway.size()>0){
				for(int a=0;a<p1[i].urlBadGateway.size();a++){ 
					retry[indice].setUrl(p1[i].urlBadGateway.get(a).toString());
					t1[indice] = new Thread(retry[indice]);
					t1[indice].start();
					indice=waitOneThreadStop(t1,nThreads);    
					}
			}
			
			if(p1[i].urlDesconhecidas.size()>0){
				for(int a=0;a<p1[i].urlDesconhecidas.size();a++){ 
					retry[indice].setUrl(p1[i].urlDesconhecidas.get(a).toString());
					t1[indice] = new Thread(retry[indice]);
					t1[indice].start();
					indice=waitOneThreadStop(t1,nThreads);    
					}
			}
			
			if(p1[i].urlErrorServer.size()>0){
				for(int a=0;a<p1[i].urlErrorServer.size();a++){ 
					retry[indice].setUrl(p1[i].urlErrorServer.get(a).toString());
					t1[indice] = new Thread(retry[indice]);
					t1[indice].start();
					indice=waitOneThreadStop(t1,nThreads);    
					}
			}
			
		}
		waitAllThreadStoped(t1,nThreads);
		System.out.println("tentei dnovo");
		return retry;
	}


	/**
	 * 
	 * @param p1
	 * @param t1
	 * @param nThreads
	 * Inicializa todas threads e a classe Programa, que a thread executa
	 */
	private void inicializaPrograma(Programa[] p1,Thread[] t1, int nThreads) {
		for(int i=0;i<nThreads;i++){
			p1[i]=new Programa();
			t1[i]= new Thread();
		}
	}

	/**
	 * 
	 * @param t1
	 * @param nthreads
	 * @throws InterruptedException
	 * Aguarda todas as threads terminarem
	 */
	private void waitAllThreadStoped(Thread[] t1,int nthreads) throws InterruptedException {
		int ThreadsTerminated=0;
		while(ThreadsTerminated<nthreads){
			ThreadsTerminated=0;
			for(int i=0;i<nthreads;i++){
				if(t1[i].getState()==Thread.State.TERMINATED||t1[i].getState()==Thread.State.NEW){
					ThreadsTerminated++;
				}
			}
			//System.out.println("Faltam :" +(-ThreadsTerminated+nthreads)+" Terminar");
			Thread.sleep(2000);
		}
		
	}

	/**
	 * 
	 * @param t1
	 * @param nThreads
	 * @return
	 * @throws InterruptedException
	 * Obtem uma o indice de uma thread nova ou desocupada
	 */
	private int waitOneThreadStop(Thread[] t1,int nThreads) throws InterruptedException {
		
		boolean flag=false;
		//System.out.println("Aguardando alguma thread terminar" );
		while(!flag)
			for(int i=0;i<nThreads;i++){
				if(t1[i].getState()==Thread.State.TERMINATED||t1[i].getState()==Thread.State.NEW){
					flag=true;
					return i;
				}
				Thread.sleep(50);
			}
		return 1;
	}


	
	/**
	 * Apresenta o resultado no console
	 */
	public void apresentaResultado(){
		System.out.println("O numero de pessoas(Links) distintas sao:" + count);
		System.out.println("O numero de links Quebrados sao:" + countQueb);
		System.out.println("O numero de links 'normais' sao:" + countOk);
		System.out.println("O numero de links Proibidos sao:" + countProibido);
		System.out.println("O numero de metodos impedidos sao:" + countImpedido);
		System.out.println("O numero de redirecionados sao:" + countRedirecionado);
		System.out.println("O numero de badGateway:" + badGateway);
		System.out.println("Erro Server:" + countErroServer);
		System.out.println("Unknown Host Name(DNS não resolve):" + countUnknownHost);
		System.out.println("O numero resposta diferente de 4.. e 200 sao:" + countPesq);
		
	}

	public static void main(String[] args) throws Exception{
		
		long antes = System.currentTimeMillis()/1000;  
		  
		ThreadsTest rdfm = new ThreadsTest();
		rdfm.manageGrafhTest();
		long tempo = System.currentTimeMillis()/1000 - antes;
		//O tempo total decorrido é mostrado aqui
		System.out.printf("O programa executou em %d Segundos.%n", tempo);

	}
	
}

