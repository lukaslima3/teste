package br.ufba.melhorado;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author patricia
 *
 */
public class CountService extends Observable {
	//Counters of broken links total number and status returned by HTTP response.
	private AtomicInteger count =new AtomicInteger();
	private AtomicInteger countQueb =new AtomicInteger();
	private AtomicInteger count200 =new AtomicInteger();
	private AtomicInteger countPesq =new AtomicInteger();
	private AtomicInteger countProibido =new AtomicInteger();
	private AtomicInteger countImpedido =new AtomicInteger();
	private AtomicInteger countRedirecionado =new AtomicInteger();
	private AtomicInteger countErroServer =new AtomicInteger();
	private AtomicInteger countUnknownHost =new AtomicInteger();
	private AtomicInteger count502 =new AtomicInteger();
	private AtomicInteger countTemp502 =new AtomicInteger();

	private static CountService uniqueInstance = new CountService();

	private CountService() {
	}

	public static synchronized CountService getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new CountService();

		return uniqueInstance;
	}


	/**************GETS and SETS*************************************/

	public AtomicInteger getCount() {
		return count;
	}

	public void setCount(AtomicInteger count) {
		this.count = count;
		
	}
	
	public void increment() {
		count.getAndIncrement();
		
		if(count.get() % 5000 == 0){  
			setChanged();
			notifyObservers();
		}  
           
   }


	public AtomicInteger getCountQueb() {
		return countQueb;
	}

	public void setCountQueb(AtomicInteger countQueb) {
		this.countQueb = countQueb;
	}

	public AtomicInteger getCountOk() {
		return count200;
	}

	public void setCountOk(AtomicInteger countOk) {
		this.count200 = countOk;
		
	}

	public AtomicInteger getCountPesq() {
		return countPesq;
	}

	public void setCountPesq(AtomicInteger countPesq) {
		this.countPesq = countPesq;
	}

	public AtomicInteger getCountProibido() {
		return countProibido;
	}

	public void setCountProibido(AtomicInteger countProibido) {
		this.countProibido = countProibido;
	}

	public AtomicInteger getCountImpedido() {
		return countImpedido;
	}

	public void setCountImpedido(AtomicInteger countImpedido) {
		this.countImpedido = countImpedido;
	}

	public AtomicInteger getCountRedirecionado() {
		return countRedirecionado;
	}

	public void setCountRedirecionado(AtomicInteger countRedirecionado) {
		this.countRedirecionado = countRedirecionado;
	}

	public AtomicInteger getCountErroServer() {
		return countErroServer;
	}

	public void setCountErroServer(AtomicInteger countErroServer) {
		this.countErroServer = countErroServer;
	}

	public AtomicInteger getCountUnknownHost() {
		return countUnknownHost;
	}

	public void setCountUnknownHost(AtomicInteger countUnknownHost) {
		this.countUnknownHost = countUnknownHost;
	}

	public AtomicInteger getCount502() {
		return count502;
	}

	public void setCount502(AtomicInteger count502) {
		this.count502 = count502;
	}

	public AtomicInteger getCountTemp502() {
		return countTemp502;
	}

	public void setCountTemp502(AtomicInteger countTemp502) {
		this.countTemp502 = countTemp502;
	}

	public static CountService getUniqueInstance() {
		return uniqueInstance;
	}

}
