package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
	private List<PowerOutagesEvent> worstEvents;
	private long maxAffected;

	public Model() {
		podao = new PowerOutageDAO();
		

	}

	public List<Nerc> getNercList() {
		return podao.getNercList();
	}

	public List<PowerOutagesEvent> worstCaseAnalysis(Nerc nerc, int maxYear, int maxHours) {

		allEvents = podao.getPowerOutagesEventsSorted(nerc);
//		allEvents.stream().forEach(a->System.out.format("%5d %s\n", a.getId(), a.getEventBegan()));
		worstEvents = null;
		maxAffected = Long.MIN_VALUE;
		
		magicFunction(new ArrayList<>(), maxYear, maxHours);

		return worstEvents;
	}

	private void magicFunction(List<PowerOutagesEvent> partial, int maxYear, int maxHours) {

		long affected = getAffected(partial);
		if (affected > maxAffected) {
			// sono nel caso migliore
			maxAffected = affected;
			worstEvents = new ArrayList<>(partial);
			System.out.format("[DEBUG] Trovata soluzione ottima-> n=%10d, %s\n", maxAffected, worstEvents);

		}

		for (PowerOutagesEvent poe : allEvents) {
			if (!partial.contains(poe)) {
				partial.add(poe);
				if (checkMaxYears(partial, maxYear, maxHours) && checkMaxHours(partial, maxYear, maxHours))
					magicFunction(partial, maxYear, maxHours);
				partial.remove(poe);
			}
		}

	}

	private boolean checkMaxHours(List<PowerOutagesEvent> partial, int maxYear, int maxHours) {
		if (partial.size() < 2)
			return true;
		long hours = 0;
		for (PowerOutagesEvent poe : partial)
			hours += Duration.between(partial.get(0).getEventBegan(), partial.get(partial.size() - 1).getEventBegan())
					.toHours();

		if (hours > maxHours)
			return false;
		else
			return true;

	}

	private boolean checkMaxYears(List<PowerOutagesEvent> partial, int maxYear, int maxHours) {
		long years = ChronoUnit.YEARS.between(partial.get(0).getEventBegan(),
				partial.get(partial.size() - 1).getEventFinished()) + 1;
		if (years > maxYear)
			return false;
		return true;
	}

	private long getAffected(List<PowerOutagesEvent> partial) {
		return partial.parallelStream().mapToLong(PowerOutagesEvent::getCostumersAffected).sum();
	}

}
