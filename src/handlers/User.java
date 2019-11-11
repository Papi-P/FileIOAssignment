package handlers;

import fileioassignment.FileIOAssignment;
import guiComponents.InputButton;
import guiComponents.InputPasswordField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/*
    @author Daniel Allen
    7-Nov-2019
 */
public final class User {
    private int failedAttempts;
    private static ArrayList<User> allUsers = new ArrayList<>();
    private final String name,
            email,
            encryptedPassword;

    static {
        try {
            readAllUsers();
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.encryptedPassword = password;
    }

    public final String getName() {
        return this.name;
    }

    public final String getEncryptedPassword() {
        return encryptedPassword;
    }

    public final String getEmail() {
        return email;
    }
    public void fail(){
        this.failedAttempts++;
    }
    public int getAttempts(){
        return this.failedAttempts;
    }
    public void success(){
        this.failedAttempts = 0;
    }
    public static final User getUserByName(String username) {
        for (User u : allUsers) {
            if (u.getName().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public static final User getUserByEmail(String email) {
        for (User u : allUsers) {
            if (u.getName().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public static final boolean hasUserByName(String username) {
        for (User u : allUsers) {
            if (u.getName().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean hasUserByEmail(String email) {
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean addUserIfAbsent(User u) {
        if (hasUserByName(u.getName())) {
            return false;
        }
        if (hasUserByEmail(u.getEmail())) {
            return false;
        }
        return allUsers.add(u);
    }

    public static final void readAllUsers() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("src\\fileioassignment\\users.dat"));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] components = line.split("\\s");
            if (components.length != 3) {
                continue;
            }
            User cur = new User(components[0], components[1], components[2]);
            addUserIfAbsent(cur);
        }
        br.close();
    }

    public static final void addUserToFile(User u) throws IOException {
        FileWriter fw = new FileWriter("src\\fileioassignment\\users.dat", true);
        fw.write(u.getName() + " " + u.getEmail() + " " + u.getEncryptedPassword() + System.lineSeparator());
        fw.close();
    }

    public static final void resetPassword(User u) {
        JDialog resetDialog = new JDialog();
        resetDialog.setModal(true);
        resetDialog.setTitle("Reset Password");
        byte[] bytes = new byte[8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Math.floor(Math.random() * 255);
        }
        String key = Encrypt.bytesToHex(bytes);
        System.out.println(key);
        try {
            EmailService.sendEmail(u.getEmail(), "Reset Password Confirmation", "<html><h1>Reset Password</h1><p>If you did not request this email, it can be ignored.</p><br><p>If not, your key to reset your password is: <b>" + key + "</b></html>");
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(FileIOAssignment.class.getName()).log(Level.SEVERE, null, ex);
        }
        JLabel informOfEmail = new JLabel("An email has been sent to the email registered to this account with a key. Please enter it below to reset your password.");
        InputPasswordField keyField = new InputPasswordField(100, 100, "Key");
        InputButton submitButton = new InputButton(60, 25) {
            @Override
            public void onClick() {
                String userKey = keyField.getText();
                if (userKey.equals(key)) {
                    try {
                        boolean validPassword = false;
                        String input = "";
                        while (!validPassword) {
                            validPassword = true;
                            input = JOptionPane.showInputDialog(resetDialog, "Enter your new password", "Reset Password", HEIGHT);
                            if (input.trim().isEmpty()) {
                                validPassword = false;
                            } else if (Verify.badPasswords.contains(input) || input.length() < 5) {
                                validPassword = false;
                            }
                            if (!validPassword) {
                                JOptionPane.showMessageDialog(resetDialog, "This password is too weak. Please create another one.", "Weak Password", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        User.removeUser(u);
                        User userWithNewPassword = new User(u.getName(), u.getEmail(), Encrypt.sha256(input));
                        allUsers.add(userWithNewPassword);
                        User.addUserToFile(userWithNewPassword);
                        JOptionPane.showMessageDialog(resetDialog, "Password was reset", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
                        resetDialog.dispose();
                    } catch (IOException ex) {
                        Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(resetDialog, "Invalid key. Check your email for the correct one.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
                    keyField.setText("");
                }
            }
        }.setText("Reset Password")
                .setAntialiased(true)
                .setCurve(15)
                .setBg(Color.decode("#38A1F3"))
                .setHoverColor(Color.decode("#38A1F3").darker())
                .setFg(Color.WHITE);

        resetDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        resetDialog.add(informOfEmail, gbc);
        gbc.gridy++;
        resetDialog.add(keyField, gbc);
        gbc.gridy++;
        resetDialog.add(submitButton, gbc);
        gbc.gridy++;
        resetDialog.setSize(600, 400);
        resetDialog.setLocationRelativeTo(null);
        resetDialog.setVisible(true);
    }

    private static final void removeUser(User user) {

        try {
            File f = new File("src\\fileioassignment\\users.dat");
            List<String> newFile = Files.lines(f.toPath()).filter(line -> !line.startsWith(user.getName() + " ")).collect(Collectors.toList());
            FileWriter fw = new FileWriter(f);
            for (String s : newFile) {
                System.out.println(s);
                fw.write(s + System.lineSeparator());
            }
            fw.flush();
            fw.close();
            allUsers.remove(user);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
