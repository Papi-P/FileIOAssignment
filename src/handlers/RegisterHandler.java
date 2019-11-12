/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import fileioassignment.GUI;
import static handlers.User.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 340900828
 */
public final class RegisterHandler {

    private final GUI gui;

    public RegisterHandler(GUI gui) {
        this.gui = gui;
    }

    //0-9 = 48-57
    //A-Z = 65-90
    //a-z = 97-122
    /**
     * Registers a user from the information in this instance's GUI
     * @return 
     */
    public boolean register() {
        String name = gui.registerNameField.getText(),
                email = gui.registerEmailField.getText(),
                fname = gui.registerFirstNameField.getText(),
                lname = gui.registerLastNameField.getText();
        //validate name
        boolean namevalid = true;
        if (name.contains(" ") || name.contains("\\s")) {
            gui.registerNameErrorLabel.setText("Spaces are not allowed");
            gui.registerNameField.setBorderColor(Color.RED);
            namevalid = false;
        } else if (name.trim().isEmpty()) {
            gui.registerNameErrorLabel.setText("Enter a username");
            gui.registerNameField.setBorderColor(Color.RED);
            namevalid = false;
        } else if (User.hasUserByName(name)) {
            gui.registerNameErrorLabel.setText("Username taken");
            gui.registerNameField.setBorderColor(Color.RED);
            namevalid = false;
        } else {
            for (char c : name.toCharArray()) {
                if (!((c <= 90 && c >= 65) || (c <= 122 && c >= 97) || (c <= 57 && c >= 48))) {
                    gui.registerNameErrorLabel.setText("Invalid characters");
                    gui.registerNameField.setBorderColor(Color.RED);
                    namevalid = false;
                }
            }
        }

        //validate email
        String allowedSpecial = "@.!#$%&'*+-/=?^_`{|}~";
        boolean emailvalid = true;
        if (email.trim().isEmpty()) {
            gui.registerEmailErrorLabel.setText("Enter your email");
            gui.registerEmailField.setBorderColor(Color.RED);
            emailvalid = false;
        } else if (User.hasUserByEmail(email)) {
            gui.registerEmailErrorLabel.setText("Email taken");
            gui.registerEmailField.setBorderColor(Color.RED);
            emailvalid = false;
        } else {
            boolean hasPeriod = false;
            boolean hasAt = false;
            for (int i = 0; i < email.toCharArray().length; i++) {
                char c = email.toCharArray()[i];
                //          between A-Z               between a-z              between 0-9                 in allowedSpecial
                if (!((c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a') || (c <= '9' && c >= '0') || allowedSpecial.indexOf(c) != -1)) {
                    gui.registerEmailErrorLabel.setText("Invalid characters");
                    gui.registerEmailField.setBorderColor(Color.RED);
                    emailvalid = false;
                }
                if (c == 46) {
                    //prevent multiple periods in a row
                    if (hasPeriod) {
                        if (email.toCharArray()[i - 1] == 46) {
                            gui.registerEmailErrorLabel.setText("Periods cannot be sequenced");
                            gui.registerEmailField.setBorderColor(Color.RED);
                            emailvalid = false;
                        }
                    }
                    //prevent a period right after @
                    if (hasAt) {
                        if (email.toCharArray()[i - 1] == 64) {
                            gui.registerEmailErrorLabel.setText("Malformed domain");
                            gui.registerEmailField.setBorderColor(Color.RED);
                            emailvalid = false;
                        }
                        hasPeriod = true;
                    }
                }
                if (c == 64) {
                    //prevent multiple @ characters
                    if (hasAt) {
                        gui.registerEmailErrorLabel.setText("Use only 1 '@'");
                        gui.registerEmailField.setBorderColor(Color.RED);
                        emailvalid = false;
                    }
                    //prevent @ right after a period
                    if (i > 0 && email.toCharArray()[i - 1] == 46) {
                        gui.registerEmailErrorLabel.setText("Malformed local-part");
                        gui.registerEmailField.setBorderColor(Color.RED);
                        emailvalid = false;
                    }
                    hasAt = true;
                }
            }
            //prevent certain invalid email formats
            if (!hasAt) {
                gui.registerEmailErrorLabel.setText("Missing '@'");
                gui.registerEmailField.setBorderColor(Color.RED);
                emailvalid = false;
            } else if (email.startsWith("@") || email.startsWith(".")) {
                gui.registerEmailErrorLabel.setText("Missing local-part");
                gui.registerEmailField.setBorderColor(Color.RED);
                emailvalid = false;
            } else if (!hasPeriod || email.endsWith(".")) {
                gui.registerEmailErrorLabel.setText("Malformed domain");
                gui.registerEmailField.setBorderColor(Color.RED);
                emailvalid = false;
            }
        }
        //validate password
        String password = String.valueOf(gui.registerPasswordField.getPassword());
        boolean passwordvalid = true;
        if (password.trim().isEmpty()) {
            gui.registerPasswordErrorLabel.setText("Enter a password");
            gui.registerPasswordField.setBorderColor(Color.RED);
            passwordvalid = false;
        } else if (Verify.badPasswords.contains(password) || password.length() < 5) {
            gui.registerPasswordErrorLabel.setText("Password is too weak!");
            gui.registerPasswordField.setBorderColor(Color.RED);
            passwordvalid = false;
        } else {
            //max sequential characters in a row
            final int maxInARow = 3;
            int curInARow = 0;
            char pre = 0;
            int scenario = 0;
            //prevent sequential characters
            for (int i = 0; i < password.length(); i++) {
                char cur = password.charAt(i);
                //prevent increasing characters
                if (cur - pre == 1) {
                    if (scenario == 1) {
                        curInARow++;
                    } else {
                        scenario = 1;
                        curInARow = 0;
                    }
                }
                //prevent decreasing characters
                else if (pre - cur == 1) {
                    if (scenario == -1) {
                        curInARow++;
                    } else {
                        scenario = -1;
                        curInARow = 0;
                    }
                }
                //prevent repeating characters
                else if (pre - cur == 0) {
                    if (scenario == 0) {
                        curInARow++;
                    } else {
                        scenario = 0;
                        curInARow = 0;
                    }
                }
                pre = cur;
                if (curInARow >= maxInARow) {
                    gui.registerPasswordErrorLabel.setText("Avoid sequential characters!");
                    gui.registerPasswordField.setBorderColor(Color.RED);
                    passwordvalid = false;
                    break;
                }
            }
        }

        if (!namevalid || !emailvalid || !passwordvalid) {
            if (namevalid) {
                gui.registerNameErrorLabel.setText("");
                gui.registerNameField.setBorderColor(Color.GREEN.darker());
            }
            if (emailvalid) {
                gui.registerEmailErrorLabel.setText("");
                gui.registerEmailField.setBorderColor(Color.GREEN.darker());
            }
            if (passwordvalid) {
                gui.registerPasswordErrorLabel.setText("");
                gui.registerPasswordField.setBorderColor(Color.GREEN.darker());
            }
        } else if (namevalid && emailvalid && passwordvalid) {
            User user = new User(name, email, Encrypt.sha256(password), fname, lname);
            password = null;
            boolean success = addUserIfAbsent(user);
            if (success) {
                try {
                    User.addUserToFile(user);
                    //clear and reset all register fields
                    gui.registerNameField.setText("");
                    gui.registerNameField.setBorderColor(Color.BLACK);
                    gui.registerEmailField.setText("");
                    gui.registerEmailField.setBorderColor(Color.BLACK);
                    gui.registerPasswordField.setText("");
                    gui.registerPasswordField.setBorderColor(Color.BLACK);
                    gui.registerFirstNameField.setText("");
                    gui.registerFirstNameField.setBorderColor(Color.BLACK);
                    gui.registerLastNameField.setText("");
                    gui.registerLastNameField.setBorderColor(Color.BLACK);
                } catch (IOException ex) {
                    success = false;
                }
            }
            return success;
        }
        return false;
    }
}
