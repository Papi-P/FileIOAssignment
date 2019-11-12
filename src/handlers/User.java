package handlers;

import fileioassignment.FileIOAssignment;
import guiComponents.InputButton;
import guiComponents.InputField;
import guiComponents.InputPasswordField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Clipboard;
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
import javax.swing.JPanel;

/*
 @author Daniel Allen
 7-Nov-2019
 */
public final class User {

    private int failedAttempts;
    private static ArrayList<User> allUsers = new ArrayList<>();
    private final String username,
            firstName,
            lastName,
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

    /**
     * Creates a new user with the specified parameters
     *
     * @param username the username
     * @param email the email
     * @param password the password (should be encrypted)
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.encryptedPassword = password;
        this.firstName = firstName == null ? "" : firstName;
        this.lastName = lastName == null ? "" : lastName;
    }

    /**
     * Gets this user's first and last name.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;index[0] = first name<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;index[1] = last name
     *
     * @return Array of the user's first and last names
     */
    public final String[] getName() {
        return new String[]{
            firstName,
            lastName
        };
    }

    /**
     * Gets this user's username
     *
     * @return this user's username
     */
    public final String getUsername() {
        return this.username;
    }

    /**
     * Gets this user's password.
     *
     * @return this user's password
     */
    public final String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Gets this user's email
     *
     * @return this user's email
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Adds a failed login attempt.
     */
    public final void fail() {
        this.failedAttempts++;
    }

    /**
     * Returns the number of failed attempts.
     *
     * @return the number of attempts
     */
    public final int getAttempts() {
        return this.failedAttempts;
    }

    /**
     * A login attempt was successful. Resets the failed attempts to 0.
     */
    protected final void success() {
        this.failedAttempts = 0;
    }

    /**
     * Gets a user from their username
     *
     * @param username the email to find
     * @return the User associated with that username, or null if one is not
     * found.
     */
    public static final User getUserByName(String username) {
        for (User u : allUsers) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Gets a user from their email
     *
     * @param email the email to find
     * @return the User associated with that email, or null if one is not found.
     */
    public static final User getUserByEmail(String email) {
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Checks if a user with the specified username exists
     *
     * @param username the name to check for
     * @return true if a user has that username
     */
    public static final boolean hasUserByName(String username) {
        for (User u : allUsers) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user with the specified email exists
     *
     * @param email the email to check for
     * @return true if a user has that email
     */
    public static final boolean hasUserByEmail(String email) {
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user exists, either by name or email. If not, it adds them.
     *
     * @param u The user to add
     * @return true if the user was added
     */
    public static final boolean addUserIfAbsent(User u) {
        if (hasUserByName(u.getUsername())) {
            return false;
        }
        if (hasUserByEmail(u.getEmail())) {
            return false;
        }
        return allUsers.add(u);
    }

    /**
     * Reads all users from the users.dat file
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static final void readAllUsers() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("src\\fileioassignment\\users.dat"));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] components = line.split(" ");
            if (components.length < 3) {
                continue;
            }
            User cur = new User(components[0], components[1], components[2], components.length > 3 ? components[3] : "", components.length > 4 ? components[4] : "");
            addUserIfAbsent(cur);
        }
        br.close();
    }

    public static final void addUserToFile(User u) throws IOException {
        FileWriter fw = new FileWriter("src\\fileioassignment\\users.dat", true);
        fw.write(u.getUsername() + " " + u.getEmail() + " " + u.getEncryptedPassword() + " " + u.getName()[0] + " " + u.getName()[1] + System.lineSeparator());
        fw.close();
    }
    static volatile boolean proceed = false;

    public static final void resetPassword(final User u) {
        proceed = false;
        JDialog resetDialog = new JDialog();
        JPanel resetPanel = new JPanel();
        resetDialog.setModal(true);
        resetDialog.setTitle("Reset Password");
        byte[] bytes = new byte[8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Math.floor(Math.random() * 255);
        }
        String key = Encrypt.bytesToHex(bytes);
        try {
            EmailService.sendEmail(u.getEmail(), "Reset Password Confirmation", "<html><h1>Reset Password</h1><p>If you did not request this email, it can be ignored.</p><br><p>If not, your key to reset your password is: <b>" + key + "</b></html>");
            JLabel informOfEmail = new JLabel("<html>An email was sent to your email, " + censorEmail(u.email, 3) + "<br>Please enter your key below to reset your password.</html>");
            InputPasswordField keyField = new InputPasswordField(160, 25, "Key");
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
                                input = JOptionPane.showInputDialog(resetPanel, "Enter your new password", "Reset Password", HEIGHT);
                                if (input.trim().isEmpty()) {
                                    validPassword = false;
                                } else if (Verify.badPasswords.contains(input) || input.length() < 5) {
                                    validPassword = false;
                                }
                                if (!validPassword) {
                                    JOptionPane.showMessageDialog(resetPanel, "This password is too weak. Please create another one.", "Weak Password", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            User.removeUser(u);
                            User userWithNewPassword = new User(u.getUsername(), u.getEmail(), Encrypt.sha256(input), u.getName()[0], u.getName()[1]);
                            allUsers.add(userWithNewPassword);
                            User.addUserToFile(userWithNewPassword);
                            JOptionPane.showMessageDialog(resetPanel, "Password was reset", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
                            resetDialog.dispose();
                        } catch (IOException ex) {
                            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        JOptionPane.showMessageDialog(resetPanel, "Invalid key. Check your email for the correct one.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
                        keyField.setText("");
                    }
                }
            }.setText("Reset Password")
                    .setAntialiased(true)
                    .setCurve(15)
                    .setBg(Color.decode("#38A1F3"))
                    .setHoverColor(Color.decode("#38A1F3").darker())
                    .setFg(Color.WHITE);

            resetPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            resetPanel.add(informOfEmail, gbc);
            gbc.gridy++;
            resetPanel.add(keyField, gbc);
            gbc.gridy++;
            resetPanel.add(submitButton, gbc);
            resetDialog.add(resetPanel);
            resetDialog.setSize(600, 400);
            resetDialog.setLocationRelativeTo(null);
            resetDialog.setVisible(true);

        } catch (MessagingException ex) {
            JDialog errorDialog = new JDialog();
            errorDialog.setAlwaysOnTop(true);
            errorDialog.setSize(600, 400);
            JLabel label = new JLabel("<html>An error occured while attempting to send an email.<br>Please enter your full email below to reset your password: </html>");
            InputField emailField = new InputField();

            InputButton submit = new InputButton("Submit") {
                @Override
                public void onClick() {
                    if (emailField.getText().equalsIgnoreCase(u.getEmail())) {
                        JDialog errorDialog = new JDialog();
                        JLabel label = new JLabel("<html>Your password reset key is: <br><b>" + key + "</b></html>");
                        InputButton copyButton = new InputButton("Copy") {
                            @Override
                            public void onClick() {
                                
                            }
                        };
                    }
                }
            };
            errorDialog.add(label);
            errorDialog.add(emailField);
            errorDialog.add(submit);
            errorDialog.setLocationRelativeTo(null);
            errorDialog.setVisible(true);

        }

    }

    private static final void removeUser(User user) {

        try {
            File f = new File("src\\fileioassignment\\users.dat");
            List<String> newFile = Files.lines(f.toPath()).filter(line -> !line.startsWith(user.getUsername() + " ")).collect(Collectors.toList());
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

    public static String censorEmail(String email, int count) {
        StringBuilder sb = new StringBuilder();
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "Invalid email";
        }
        if (count < 0 || count > parts[0].length()) {
            throw new IllegalArgumentException("Count must be greater than 0 and cannot be greater than the length of the email");
        }
        for (int i = 0; i < parts[0].length() - count; i++) {
            sb.append("*");
        }
        sb.append(email.substring(parts[0].length() - count));
        return sb.toString();
    }
}
