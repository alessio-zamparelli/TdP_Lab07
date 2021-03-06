package it.polito.tdp.poweroutages.db;

import java.util.List;

import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutagesEvent;

public class TestDAO {

	public static void main(String[] args) {
		TestDAO t = new TestDAO();
		PowerOutageDAO dao = new PowerOutageDAO();
		t.run(dao);
	}
	
	private void run(PowerOutageDAO dao) {
//		System.out.println(dao.getPowerOutagesEvents(new Nerc(2, null)));
		List<PowerOutagesEvent> res = dao.getPowerOutagesEvents(new Nerc(8, null));
		res.stream().forEach(a->System.out.println(a.toString() + "\n"));
		System.out.println("dimensione lista: " + res.size());
	}
}
