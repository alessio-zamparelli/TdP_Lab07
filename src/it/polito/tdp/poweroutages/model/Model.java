package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
//import java.time.temporal.TemporalUnit;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.polito.tdp.poweroutages.db.PowerOutageDAO;
import sun.util.logging.resources.logging;

public class Model {

	private PowerOutageDAO podao;

	// per la funzione ricorsiva
	private List<PowerOutagesEvent> allEvents;

	private List<PowerOutagesEvent> solution;
	private int maxAffected;

	public Model() {
		podao = new PowerOutageDAO();
	}

	public List<Nerc> getNercList() {
		return podao.getNercList();
	}

	public List<PowerOutagesEvent> worstCaseAnalysis(Nerc nerc, int maxYear, int maxHours) {

		allEvents = podao.getPowerOutagesEventsSorted(nerc);

//		allEvents.stream().forEach(a->System.out.format("%5d %s\n", a.getId(), a.getEventBegan()));
		solution = null;
		maxAffected = 0;

		System.out.println("Valori iniziali:");
		allEvents.stream().forEach(a -> System.out.println(a.toString()));

		magicFunction(new ArrayList<>(), maxYear, maxHours);

		return solution;
	}

	private void magicFunction(List<PowerOutagesEvent> partial, int maxYear, int maxHours) {

//		if (partial != null && partial.size() != 0)
//			System.out.format("\n\n[DEBUG]    Parti -> %s\n",
//					partial.stream().map(a -> a.getId()).collect(Collectors.toList()));

		int affected = getAffected(partial);
		if (affected > maxAffected) {
			// sono nel caso peggiore
			maxAffected = affected;
			solution = new ArrayList<>(partial);
//			System.out.format("[DEBUG] NEW BEST -> %s  MaxAff %d\n",
//					solution.stream().map(a -> a.getId()).collect(Collectors.toList()), maxAffected);

		}

		for (PowerOutagesEvent poe : allEvents) {
			if (!partial.contains(poe)) {

				partial.add(poe);

//				System.out.format("Valuto: [%s] da inserire in: %s\n", poe.getId(),
//						partial.stream().map(a -> a.getId()).collect(Collectors.toList()));
				if (checkMaxYears(partial, maxYear) && checkMaxHours(partial, maxHours)) {
//					System.out.format("[DEBUG] newParti -> %s\n",
//							partial.stream().map(a -> a.getId()).collect(Collectors.toList()));
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {}
					magicFunction(partial, maxYear, maxHours);
				}

				partial.remove(poe);
			}
		}

	}

	public int getTotalHours(List<PowerOutagesEvent> partial) {
		int hours = 0;
		for (PowerOutagesEvent poe : partial)
			hours += poe.getOutageDuration();
		return hours;
	}

	private boolean checkMaxHours(List<PowerOutagesEvent> partial, int maxHours) {

		int hours = getTotalHours(partial);

		if (hours > maxHours) {
//			System.out.format("\tNO ORE -> actual: %d max: %d\n", hours, maxHours);
			return false;
		} else {
//			System.out.format("\tSI ORE -> actual: %d max: %d\n", hours, maxHours);
			return true;
		}

	}

	private boolean checkMaxYears(List<PowerOutagesEvent> partial, int maxYear) {
		int year1 = partial.get(0).getYear();
		int year2 = partial.get(partial.size() - 1).getYear();

		if ((year2 - year1 + 1) > maxYear) {
//			System.out.format("\tNO ANNI-> actual: %d max: %d\n", (year2 - year1 + 1), maxYear);
			return false;
		} else {
//			System.out.format("\tSI ANNI-> actual: %d max: %d\n", (year2 - year1 + 1), maxYear);
			return true;
		}
	}

	public int getAffected(List<PowerOutagesEvent> partial) {
//		return partial.parallelStream().mapToLong(PowerOutagesEvent::getCostumersAffected).sum();
		int sum = 0;
		for (PowerOutagesEvent poe : partial)
			sum += poe.getCostumersAffected();
		return sum;
	}

	public List<Integer> getYearsList() {
		return podao.getAllPowerOutagesEvents().stream().map(a->a.getYear()).distinct().sorted().collect(Collectors.toList());
	}

}
