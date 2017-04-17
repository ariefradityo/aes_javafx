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
import java.util.List;
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
    }

    public void onEncryptClicked() throws IOException {
        SecretKeySpec key = new SecretKeySpec(keyByteArray, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ivBytes = cipher.getIV();

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bOut.write(inputFile.getName().concat(";").getBytes());
            bOut.write(inputByteArray);

            byte[] newInputByteArray = bOut.toByteArray();
            bOut.reset();

            ByteArrayInputStream bIn = new ByteArrayInputStream(newInputByteArray);
            CipherInputStream cIn = new CipherInputStream(bIn, cipher);

            int ch;
            while ((ch = cIn.read()) >= 0) {
                bOut.write(ch);
            }

            bOut.write(ivBytes);
            byte[] cipherText = bOut.toByteArray();
            bOut.close();
            bIn.close();
            cIn.close();

            FileUtils.writeByteArrayToFile(new File("cipher.txt"), cipherText);
            showSuccess("Encryption", "Encryption is successful!");

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            showErrorAlert("Encryption Error!", "Invalid key!");
        } catch (Exception e) {
            showErrorAlert("Encryption Error!", "Enkripsi failed! :(");
        }

    }

    public void onDecryptClicked() throws InvalidKeyException, IOException {
        int n = inputByteArray.length;
        byte[] ivBytes = Arrays.copyOfRange(inputByteArray, n - 16, n);

        byte[] newInputByteArray = Arrays.copyOfRange(inputByteArray, 0, n - 16);
        SecretKeySpec key = new SecretKeySpec(keyByteArray, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            CipherOutputStream cOut = new CipherOutputStream(bOut, cipher);
            cOut.write(newInputByteArray);
            cOut.close();

            String outputString = bOut.toString();
            byte[] outputArray = bOut.toByteArray();
            bOut.close();
            int indexOfDelimiter = outputString.indexOf(';');
            String fileName = outputString.substring(0, indexOfDelimiter);
            outputArray = Arrays.copyOfRange(bOut.toByteArray(), indexOfDelimiter + 1, outputArray.length);

            FileUtils.writeByteArrayToFile(new File(fileName), outputArray);

            showSuccess("Decryption", "Decryption is successful!");

        } catch (Exception e) {
            showErrorAlert("Decryption Error!", "Decryption failed! :(");
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

    private void showSuccess(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
