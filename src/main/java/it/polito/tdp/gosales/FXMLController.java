package it.polito.tdp.gosales;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.gosales.model.Model;
import it.polito.tdp.gosales.model.Products;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {

    private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCerca;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private ComboBox<Integer> cmbAnno;

    @FXML
    private ComboBox<String> cmbBrand;

    @FXML
    private ComboBox<Integer> cmbProdotto;

    @FXML
    private TextArea txtArchi;

    @FXML
    private TextArea txtResGrafo;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCercaPercorso(ActionEvent event) {

        txtResult.clear();

        Integer prodotto = cmbProdotto.getSelectionModel().getSelectedItem();

        if (prodotto == null) {
            txtResult.appendText("Per favore seleziona un prodotto!\n");
            return;
        }

        List<Products> percorso = this.model.percorso(prodotto);

        txtResult.appendText("Percorso trovato:\n");
        txtResult.appendText("Numero di archi: " + (percorso.size() - 1) + "\n\n");

        for (int i = 0; i < percorso.size(); i++) {

            Products prodottoCorrente = percorso.get(i);

            txtResult.appendText(prodottoCorrente.toString() + "\n");

            if (i < percorso.size() - 1) {

                Products prodottoSuccessivo = percorso.get(i + 1);

                double peso = model.getPesoArco(
                        prodottoCorrente,
                        prodottoSuccessivo);

                txtResult.appendText("   | Peso arco: " + peso + "\n");
                txtResult.appendText("   v\n");
            }
        }
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {

        txtResGrafo.clear();
        txtArchi.clear();
        txtResult.clear();

        Integer anno = cmbAnno.getSelectionModel().getSelectedItem();
        if (anno == null) {
            txtResGrafo.appendText("Perfavore seleziona un anno!\n");
            return;
        }

        String brand = cmbBrand.getSelectionModel().getSelectedItem();
        if (brand == null) {
            txtResGrafo.appendText("Perfavore seleziona un brand!\n");
            return;
        }

        this.model.creaGrafo(brand, anno);
        txtResGrafo.appendText("Grafo creato!\n");
        txtResGrafo.appendText("#VERTICI: " + this.model.numeroVertici() + "\n");
        txtResGrafo.appendText("#ARCHI: " + this.model.numeroArchi() + "\n");
        txtArchi.appendText(this.model.archiMaggiori());

        cmbProdotto.getSelectionModel().clearSelection();
        cmbProdotto.getItems().clear();
        cmbProdotto.getItems().addAll(this.model.listaVertici());

    }

    @FXML
    void initialize() {
        assert btnCerca != null : "fx:id=\"btnCerca\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBrand != null : "fx:id=\"cmbBrand\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProdotto != null : "fx:id=\"cmbProdotto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtArchi != null : "fx:id=\"txtArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResGrafo != null : "fx:id=\"txtResGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

    public void setModel(Model model) {
        this.model = model;
        cmbAnno.getItems().addAll(this.model.listaAnni());
        cmbBrand.getItems().addAll(this.model.listaBrand());
    }

}
