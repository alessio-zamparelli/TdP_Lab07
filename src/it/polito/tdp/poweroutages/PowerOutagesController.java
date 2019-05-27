package it.polito.tdp.poweroutages;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.poweroutages.model.Model;
import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutagesEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PowerOutagesController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextArea txtResult;

	@FXML
	private ComboBox<Nerc> cmbNerc;

	@FXML
	private TextField txtYears;

	@FXML
	private TextField txtHours;

	private Model model;

	@FXML
	void doRun(ActionEvent event) {

		Nerc selectedNerc = cmbNerc.getSelectionModel().getSelectedItem();
		if(selectedNerc==null) {
			txtResult.setText("Selezionare un NERC nel menu a tendina");
			return;
		}
		int maxYears;
		try {
			maxYears = Integer.parseInt(txtYears.getText());
			List<Integer>yearsList = model.getYearsList();
			if(maxYears==0||maxYears>yearsList.size()+1) {
				System.out.println("Inserire un numero di anni tra 1 e " + yearsList.size());
				return;
			}
		} catch (NumberFormatException e) {
			txtResult.setText("Numero di anni non validi");
			return;
		}
		int maxHours;
		try {
			maxHours = Integer.parseInt(txtHours.getText());
		} catch (NumberFormatException e) {
			txtResult.setText("Numero di ore non valido");
			return;
		}
		
		List<PowerOutagesEvent> res = model.worstCaseAnalysis(selectedNerc, maxYears, maxHours);
		
		txtResult.setText(String.format("Massimo numero di persone: %d\nMassime ore: %d\nMassime citta: %d\n", model.getAffected(res), model.getTotalHours(res), res.size()));
		for(PowerOutagesEvent poe : res) {
			txtResult.appendText(poe.toString() + "\n");
		}
		
		
		
		
		
	}

	@FXML
	void initialize() {
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert cmbNerc != null : "fx:id=\"cmbNerc\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert txtYears != null : "fx:id=\"txtYears\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert txtHours != null : "fx:id=\"txtHours\" was not injected: check your FXML file 'PowerOutages.fxml'.";

		// Utilizzare questo font per incolonnare correttamente i dati
		txtResult.setStyle("-fx-font-family: monospace");
	}

	public void setModel(Model model) {

		this.model = model;
		List<Nerc> nercList = model.getNercList();

		cmbNerc.getItems().addAll(nercList);
	}
}