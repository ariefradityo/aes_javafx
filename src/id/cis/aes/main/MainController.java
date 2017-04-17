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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
            //inputByteArray = FileUtils.readFileToString(inputFile, Charset.defaultCharset()).getBytes();
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

            if (!(keyString.length() == 32 || keyString.length() == 48 || keyString.length() == 64)) {
                throw new LengthException();
            }

            this.keyByteArray = Hex.decodeHex(keyString.toCharArray());

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

        if (keyFile == null) return;

        mLabelKey.setText(keyFile.getName());
        System.out.println();
    }

    public void onEncryptClicked() throws IOException {
        SecretKeySpec key = new SecretKeySpec(keyByteArray, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ivBytes = cipher.getIV();
            ByteArrayInputStream bIn = new ByteArrayInputStream(inputByteArray);
            CipherInputStream cIn = new CipherInputStream(bIn, cipher);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            int ch;
            while ((ch = cIn.read()) >= 0) {
                bOut.write(ch);
            }

            bOut.write(ivBytes);
            byte[] cipherText = bOut.toByteArray();

            FileUtils.writeByteArrayToFile(new File("cipher.txt"), cipherText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public void onDecryptClicked() throws InvalidKeyException, IOException {
        int n = inputByteArray.length;
        byte[] ivBytes = Arrays.copyOfRange(inputByteArray, n - 16, n);
        System.out.println(ivBytes.length);
        byte[] newInputByteArray = Arrays.copyOfRange(inputByteArray, 0, n - 17);
        SecretKeySpec key = new SecretKeySpec(keyByteArray, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            CipherOutputStream cOut = new CipherOutputStream(bOut, cipher);
            cOut.write(newInputByteArray);
            cOut.close();
            // to do
            // write to file
            //System.out.println("plain : " + new String(bOut.toByteArray()));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
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
