package it.polito.tdp.poweroutages.model;

import java.util.List;
import java.util.Set;

import it.polito.tdp.poweroutages.db.PowerOutageDAO;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();
		PowerOutageDAO dao = new PowerOutageDAO();

		List<Nerc> nercList = model.getNercList();

//		System.out.println(nercList);

		Set<PowerOutagesEvent> idMapEvents = dao
				.getPowerOutagesEventsSorted(nercList.stream().filter(a -> a.getId() == 1).findAny().orElse(null));
//		idMapEvents.stream().forEach(a->System.out.println(a));
		

		Set<PowerOutagesEvent> res = model
				.worstCaseAnalysis(nercList.stream().filter(a -> a.getId() == 1).findAny().orElse(null), 3, 32);
		System.out.println("[INFO] Sono alla fine");
		System.out.println(res);
	}
}
