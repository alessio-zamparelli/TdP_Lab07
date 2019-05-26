package it.polito.tdp.poweroutages;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import it.polito.tdp.poweroutages.model.Model;
import it.polito.tdp.poweroutages.model.Nerc;

public class PowerOutagesController_OLD {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ComboBox<Nerc> boxNerc;

	@FXML
	private GridPane gridPane;

	@FXML
	private TextField txtYears;

	@FXML
	private TextField txtHours;

	@FXML
	private TextArea txtResult;

	@FXML
	void initialize() {
		assert boxNerc != null : "fx:id=\"boxNerc\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert txtYears != null : "fx:id=\"txtYears\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert txtHours != null : "fx:id=\"txtHours\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'PowerOutages.fxml'.";

		txtResult.setStyle("-fx-font-family: monospace");

	}

	public void setModel(Model model) {
		this.model = model;

		boxNerc.getItems().setAll(model.getNercList());
	}

}
