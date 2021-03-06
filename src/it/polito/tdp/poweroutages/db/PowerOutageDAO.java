package it.polito.tdp.poweroutages.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutagesEvent;

public class PowerOutageDAO {

	public List<Nerc> getNercList() {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Nerc n = new Nerc(rs.getInt("id"), rs.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}

	public List<PowerOutagesEvent> getPowerOutagesEvents(Nerc nerc) {
		String sql = "SELECT customers_affected, date_event_began, date_event_finished, id FROM poweroutages "
				+ "WHERE nerc_id = ? ORDER BY date_event_began";
		List<PowerOutagesEvent> result = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nerc.getId());
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				PowerOutagesEvent event = new PowerOutagesEvent(new Timestamp(rs.getDate("date_event_began").getTime()).toLocalDateTime(),
						new Timestamp(rs.getDate("date_event_finished").getTime()).toLocalDateTime(), rs.getDouble("customers_affected"), rs.getInt("id"));
				result.add(event);
			}
			

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public Set<PowerOutagesEvent> getPowerOutagesEventsSorted(Nerc nerc) {
			String sql = "SELECT customers_affected, date_event_began, date_event_finished, id FROM poweroutages "
					+ "WHERE nerc_id = ? ORDER BY date_event_began";
			Set<PowerOutagesEvent> result = new TreeSet<>();

			try {
				Connection conn = ConnectDB.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, nerc.getId());
				ResultSet rs = st.executeQuery();
				
				while (rs.next()) {
					PowerOutagesEvent event = new PowerOutagesEvent(new Timestamp(rs.getDate("date_event_began").getTime()).toLocalDateTime(),
							new Timestamp(rs.getDate("date_event_finished").getTime()).toLocalDateTime(), rs.getDouble("customers_affected"), rs.getInt("id"));
					result.add(event);
				}
				

				conn.close();

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			return result;
		}
	}


