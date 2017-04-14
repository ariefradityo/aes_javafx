package id.cis.aes.main;

import id.cis.aes.exception.LengthException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane mAnchorPane;

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

    private Stage mainStage;

    private File inputFile;
    private File keyFile;

    byte[] keyByteArray;
    byte[] inputByteArray;

    public void initialize(URL location, ResourceBundle resources) {
        mLabelMadeBy.setText("Dibuat oleh:\nAndri Kurniawan (13063)\nArief Radityo (1306381875)");
    }

    public void onInputBrowseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Input File");
        this.inputFile = fileChooser.showOpenDialog(mainStage);

        mLabelInput.setText(inputFile.getName());

        try {
            inputByteArray = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onKeyBrowseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Key File");
        this.keyFile = fileChooser.showOpenDialog(mainStage);

        try {
            String keyString = FileUtils.readFileToString(keyFile, Charset.defaultCharset());

            if(!(keyString.length() == 32 || keyString.length() == 48 || keyString.length() == 64)){
                throw new LengthException();
            }

            this.keyByteArray = Hex.decodeHex(keyString.toCharArray());

            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            showErrorAlert("Decoding Error", "Pastikan file key berisi karakter hexadecimal");
            this.keyFile = null;
            e.printStackTrace();
        } catch (LengthException e) {
            showErrorAlert("Decoding Error", "Pastikan panjang key 128, 192 atau 256 bit");
            this.keyFile = null;
        }

        if(keyFile == null) return;

        mLabelKey.setText(keyFile.getName());
        System.out.println();
    }

    public void onEncryptClicked() {

    }

    public void onDecryptClicked() {

    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
