package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class RjesenjeContoller {
    public Button pocetakButton;
    public ImageView slika;
    public Label rjesenjeLabel;
    public Label opisLabel;
    private ArrayList<Cvor> postarovPut = new ArrayList<>();
    public RjesenjeContoller(ArrayList<Cvor> postarovPut) {
        this.postarovPut = postarovPut;
    }

    @FXML
    public void initialize() {
        Image postarSlika = new Image("/images/pronasaoPut.png");
        slika.setImage(postarSlika);
        slika.setFitWidth(274);
        slika.setFitHeight(280);
        String put = "";
        for(int i = 0; i < postarovPut.size(); i++) {
            if(i == postarovPut.size() - 1) put += postarovPut.get(i);
            else put += postarovPut.get(i) + " - ";
        }
        rjesenjeLabel.setText(put);
        rjesenjeLabel.setAlignment(Pos.TOP_CENTER);
    }

    public void pocetakAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) pocetakButton.getScene().getWindow();
        Controller ctrl = new Controller();
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/fxml/pocetna.fxml"));
        loader.setController(ctrl);
        Parent root = loader.load();
        Stage pocetna = new Stage();
        pocetna.setTitle("Kineski problem poštara");
        pocetna.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.close();
        pocetna.show();
    }
}
