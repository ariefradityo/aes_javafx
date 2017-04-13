package id.cis.aes.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML
    private Button mBtnBrowseInput;

    @FXML
    private Label mLabelInput;

    @FXML
    private Button mBtnBrowseKey;

    @FXML
    private Label mLabelKey;

    @FXML
    private Button mBtnEncrypt;

    @FXML
    private Button mBtnDecrypt;

    @FXML
    private Label mLabelMadeBy;

    public void initialize(URL location, ResourceBundle resources) {
        mLabelMadeBy.setText("Dibuat oleh:\nAndri Kurniawan (13063)\nArief Radityo (1306381875)");
    }

    public void onInputBrowseClicked(){

    }

    public void onKeyBrowseClicked(){

    }

    public void onEncryptClicked(){

    }

    public void onDecryptClicked(){

    }
}
