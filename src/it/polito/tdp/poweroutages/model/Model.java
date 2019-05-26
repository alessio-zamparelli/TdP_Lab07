package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
//import java.time.temporal.TemporalUnit;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
//import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.polito.tdp.poweroutages.db.PowerOutageDAO;

public class Model {

	private PowerOutageDAO podao;

	// per la funzione ricorsiva
	private Case solution;
	

	public Model() {
		podao = new PowerOutageDAO();

	}

	public List<Nerc> getNercList() {
		return podao.getNercList();
	}

	public Set<PowerOutagesEvent> worstCaseAnalysis(Nerc nerc, int maxYear, int maxHours) {

//		Map<Integer, PowerOutagesEvent> idMapEvents = new HashMap<>();

//		for (PowerOutagesEvent poe : podao.getPowerOutagesEvents(nerc)) {
//			idMapEvents.put(poe.getId(), poe);
//		}
		
		Set<PowerOutagesEvent> idMapEvents = podao.getPowerOutagesEventsSorted(nerc);
//		System.out.println(idMapEvents.values());
		solution = new Case(false, LocalDateTime.MAX, LocalDateTime.MIN, new TreeSet<PowerOutagesEvent>(), Long.MIN_VALUE);
		
		magicFunction(new TreeSet<PowerOutagesEvent>(), maxYear, maxHours, idMapEvents);
		
		return solution.getEvents();
	}

	private void magicFunction(Set<PowerOutagesEvent> partial, int maxYear, int maxHours, Set<PowerOutagesEvent> events) {

		if(events.size()==0)
			// fine dei giochi
			return;
		
		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
		
		System.out.format("\n\n");
		
		System.out.format("[DEBUG] partial: %s\n", partial.stream().map(a->a.getId()).collect(Collectors.toList()));
		
		Case partialCase = partialValid(partial, maxYear, maxHours);
		
		if(solution.getEvents()!=null)
			System.out.format("[DEBUG] best   : %s persone: %f\n", solution.getEvents().stream().map(a->a.getId()).collect(Collectors.toList()), solution.getMaxPeopleAffected());
		
		
	
		if(!partialCase.isValid)
			// controllo sulla validità della soluzione parziale
			return;
		
		if(partialCase.maxPeopleAffected > solution.maxPeopleAffected) {
			// questo è il caso peggiore (quindi quello che cerco)
			solution = partialCase;
			System.out.format("[DEBUG] NEW BEST, Valid:%b\n", solution.isValid());
			if(solution.getEvents()!=null)
				System.out.format("[DEBUG] best   : %s persone: %.0f\n", solution.getEvents().stream().map(a->a.getId()).collect(Collectors.toList()), solution.getMaxPeopleAffected());
		}
		
		for (PowerOutagesEvent e : events) {

			Set<PowerOutagesEvent> newPartial = new TreeSet<>(partial);
			newPartial.add(e);
			Set<PowerOutagesEvent> newEvents = new TreeSet<>(events);
			newEvents.remove(e);
			
			magicFunction(newPartial, maxYear, maxHours, newEvents);
			
		}

	}



	private Case partialValid(Set<PowerOutagesEvent> partial, int maxYear, int maxHours) {
		
		LocalDateTime yearBegan = LocalDateTime.MAX;
		LocalDateTime yearEnd = LocalDateTime.MIN;
		long hours = 0;
		long maxPeopleAffected = 0;
		
		
		if(partial.size()==0) {
			System.out.format("[DEBUG] dimensione parz == 0\n");
			return new Case(true);

		}
		
		for(PowerOutagesEvent poe: partial) {
			// se inizia prima
			if(poe.getEventBegan().isBefore(yearBegan))
				yearBegan = poe.getEventBegan();
			// se finisce dopo
			if(poe.getEventFinished().isAfter(yearEnd))
				yearEnd = poe.getEventFinished();
			
			hours += Duration.between(poe.getEventBegan(), poe.getEventFinished()).toHours();
			maxPeopleAffected+=poe.getCostumersAffected();
		}		
		// controllo di non aver sforato gli anni
		if(ChronoUnit.YEARS.between(yearBegan, yearEnd)>maxYear) {
			System.out.format("[DEBUG] NO Anni: %d Max:%d\n", ChronoUnit.YEARS.between(yearBegan, yearEnd), maxYear);
			return new Case(false);
		}
		// controllo di non aver sforato le ore
		if(hours>maxHours) {
			System.out.format("[DEBUG] NO Ore: %d Max:%d\n", hours, maxHours);
			return new Case(false);

		}
		
		System.out.format("[DEBUG] SI Pers: %d Anni: %d Max: %d Ore: %d Max: %d\n",maxPeopleAffected, ChronoUnit.YEARS.between(yearBegan, yearEnd), maxYear, hours, maxHours);

		return new Case(true, yearBegan, yearEnd, partial, maxPeopleAffected);
	}
	
	private class Case {
		// mi serviva un wrapper! (bruttino pero cosi...)
		private boolean isValid;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private long maxPeopleAffected;
		private Set<PowerOutagesEvent> events;
		
		public Case(boolean isValid, LocalDateTime startDate, LocalDateTime endDate, Set<PowerOutagesEvent> events, long maxPeopleAffected) {
			this.isValid = isValid;
			this.startDate = startDate;
			this.endDate = endDate;
			this.events = events;
			this.maxPeopleAffected = maxPeopleAffected;
		}

		public Case(boolean isValid) {
			this.isValid = isValid;
//			this.startDate = LocalDateTime.MAX;
//			this.endDate = LocalDateTime.MIN;
//			this.events = null;
//			this.maxPeopleAffected=0;
			
		}

		public Set<PowerOutagesEvent> getEvents() {
			return events;
		}

		public boolean isValid() {
			return isValid;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public LocalDateTime getEndDate() {
			return endDate;
		}
		
		public double getMaxPeopleAffected() {
			return maxPeopleAffected;
		}

		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}

		public void setStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public void setEndDate(LocalDateTime endDate) {
			this.endDate = endDate;
		}

		
//		public void setEvents(Map<Integer, PowerOutagesEvent> events) {
//			this.events = events;
//		}
		
		public void setMaxPeopleAffected(long maxPeopleAffected) {
			this.maxPeopleAffected = maxPeopleAffected;
		}

		@Override
		public String toString() {
			return String.format("Case [isValid=%s, startDate=%s, endDate=%s, maxPeopleAffected=%s, events=%s]",
					isValid, startDate, endDate, maxPeopleAffected, events);
		}
		
		
		
		
		
	}

}
