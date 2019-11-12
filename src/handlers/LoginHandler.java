/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import fileioassignment.GUI;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author 340900828
 */
public final class LoginHandler {

    private final GUI gui;

    public LoginHandler(GUI gui) {
        this.gui = gui;
    }

    public boolean login() {
        User user = User.getUserByName(gui.loginNameField.getText());
        if (user != null && (!String.valueOf(gui.loginPasswordField.getPassword()).trim().isEmpty() && !gui.loginNameField.getText().trim().isEmpty())) {

            gui.loginNameErrorLabel.setText("");
            gui.loginNameField.setBorderColor(Color.BLACK);

            if (Encrypt.sha256(String.valueOf(gui.loginPasswordField.getPassword())).equals(user.getEncryptedPassword())) {
                user.success();
                JOptionPane.showMessageDialog(gui, "Welcome, " + user.getUsername(), "Program that does stuff", JOptionPane.PLAIN_MESSAGE);
                gui.loginPasswordErrorLabel.setText("");
                gui.loginPasswordField.setBorderColor(Color.BLACK);
                return true;
            } else {
                gui.loginPasswordErrorLabel.setText("Check your login credentials.");
                gui.loginPasswordField.setBorderColor(Color.RED);
                user.fail();
                if (user.getAttempts() >= 4) {
                    try {
                        //send the owner of the account an email informing them of the attempt
                        EmailService.sendEmail(user.getEmail(), "Failed Login Attempts", "<b>There have been <u>" + user.getAttempts() + "</u> failed login attempts on your account, \"" + user.getUsername() + "\". </b><br><p>If you have forgotten your password you can reset it in the program.</p>");
                        //close the program to stall the user.
                        SwingUtilities.invokeLater(()-> {
                            gui.dispose();
                        });
                    } catch (IOException | MessagingException ex) {
                        Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return false;
            }
        } else {
            if (gui.loginNameField.getText().trim().isEmpty()) {
                gui.loginNameErrorLabel.setText("Enter your username");
                gui.loginNameField.setBorderColor(Color.RED);
            } else if (user == null) {
                gui.loginNameErrorLabel.setText("Invalid username");
                gui.loginNameField.setBorderColor(Color.RED);
            } else {
                gui.loginNameErrorLabel.setText("");
                gui.loginNameField.setBorderColor(Color.GREEN);
            }

            if (String.valueOf(gui.loginPasswordField.getPassword()).trim().isEmpty()) {
                gui.loginPasswordErrorLabel.setText("Enter your password");
                gui.loginPasswordField.setBorderColor(Color.RED);
            } else if (user != null) {
                gui.loginPasswordErrorLabel.setText("");
                gui.loginPasswordField.setBorderColor(Color.GREEN);
            } else {
                gui.loginPasswordErrorLabel.setText("");
                gui.loginPasswordField.setBorderColor(Color.BLACK);
            }
        }
        return false;
    }

}