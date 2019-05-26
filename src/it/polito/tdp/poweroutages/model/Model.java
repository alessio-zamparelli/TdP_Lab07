package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.poweroutages.db.PowerOutageDAO;

public class Model {

	private PowerOutageDAO podao;

	private NercIdMap nercIdMap;
	private List<Nerc> nercList;

	private List<PowerOutageEvent> eventList;
	private List<PowerOutageEvent> eventListFiltered;

	private List<PowerOutageEvent> solution;
	private int maxAffectedPeople;

	public Model() {

		podao = new PowerOutageDAO();

		nercIdMap = new NercIdMap();
		nercList = podao.getNercList(nercIdMap);
		System.out.println(nercList);

		eventList = podao.getPowerOutageEventList(nercIdMap);
		System.out.println(eventList);
	}

	// Add the nerc parameter
	public List<PowerOutageEvent> getWorstCase(int maxNumberOfYears, int maxHoursOfOutage, Nerc nerc) {

		// Initialization phase
		solution = new ArrayList<>();
		maxAffectedPeople = 0;

		// Create new eventListFiltered
		// seleziona praticamente quelli presi dall'input
		eventListFiltered = new ArrayList<>();
		for (PowerOutageEvent event : eventList) {
			if (event.getNerc().equals(nerc)) {
				eventListFiltered.add(event);
			}
		}

		// Li ordina come ho fatto io per data di inizio
		eventListFiltered.sort(new Comparator<PowerOutageEvent>() {
			@Override
			public int compare(PowerOutageEvent o1, PowerOutageEvent o2) {
				return o1.getOutageStart().compareTo(o2.getOutageStart());
			}

		});

		System.out.println("Event list filtered: " + eventListFiltered);

		recursive(new ArrayList<PowerOutageEvent>(), maxNumberOfYears, maxHoursOfOutage);

		return solution;
	}

	public int sumAffectedPeople(List<PowerOutageEvent> partial) {
		int sum = 0;
		for (PowerOutageEvent event : partial) {
			sum += event.getAffectedPeople();
		}
		return sum;
	}

	private boolean checkMaxYears(List<PowerOutageEvent> partial, int maxNumberOfYears) {
		if (partial.size() >= 2) {
			int y1 = partial.get(0).getYear();
			int y2 = partial.get(partial.size() - 1).getYear();
			if ((y2 - y1 + 1) > maxNumberOfYears) {
				return false;
			}
		}
		return true;
	}

	public int sumOutageHours(List<PowerOutageEvent> partial) {
		int sum = 0;
		for (PowerOutageEvent event : partial) {
			sum += event.getOutageDuration();
		}
		return sum;
	}

	private boolean checkMaxHoursOfOutage(List<PowerOutageEvent> partial, int maxHoursOfOutage) {
		int sum = sumOutageHours(partial);
		if (sum > maxHoursOfOutage) {
			return false;
		}
		return true;
	}

	private void recursive(List<PowerOutageEvent> partial, int maxNumberOfYears, int maxHoursOfOutage) {

		// Update the best solution if needed
		if (sumAffectedPeople(partial) > maxAffectedPeople) {
			maxAffectedPeople = sumAffectedPeople(partial);
			solution = new ArrayList<PowerOutageEvent>(partial);
		}

		for (PowerOutageEvent event : eventListFiltered) {

			// Partial must not contains the same event
			if (!partial.contains(event)) {

				partial.add(event);

				// Controlla se la soluzione è corretta prima di scendere in ricorsione
				if (checkMaxYears(partial, maxNumberOfYears) && checkMaxHoursOfOutage(partial, maxHoursOfOutage)) {

					recursive(partial, maxNumberOfYears, maxHoursOfOutage);
				}

				partial.remove(event);
			}
		}
	}

	public List<Nerc> getNercList() {
		return this.nercList;
	}

	// questo serve per verifica al controller
	public List<Integer> getYearList() {
		Set<Integer> yearSet = new HashSet<Integer>();
		for (PowerOutageEvent event : eventList) {
			yearSet.add(event.getYear());
		}
		List<Integer> yearList = new ArrayList<Integer>(yearSet);
		yearList.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}

		});
		return yearList;
	}

}
