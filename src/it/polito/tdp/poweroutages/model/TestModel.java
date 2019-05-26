package it.polito.tdp.poweroutages.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		
		List<Nerc> nercList = model.getNercList();
		
//		System.out.println(nercList);
		
		List<PowerOutagesEvent> res = model.worstCaseAnalysis(nercList.stream().filter(a->a.getId()==1).findAny().orElse(null), 3, 32);
		System.out.println("[INFO] Sono alla fine");
		System.out.println(res);
	}
}
