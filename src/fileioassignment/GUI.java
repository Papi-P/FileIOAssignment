/*
 * Â© 2019 Daniel Allen
 */
package fileioassignment;

import guiComponents.InputButton;
import guiComponents.InputField;
import guiComponents.InputPasswordField;
import handlers.LoginHandler;
import handlers.RegisterHandler;
import handlers.User;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 *
 * @author Daniel Allen
 */
public class GUI extends JFrame {

    private LoginHandler loginHandler = new LoginHandler(this);
    private RegisterHandler registerHandler = new RegisterHandler(this);

    private GridBagLayout gbl = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();
    JPanel loginPanel = new JPanel();
    JPanel registerPanel = new JPanel();

    //login components
    JLabel loginNameLabel = new JLabel("Username:");
    public JLabel loginNameErrorLabel = new JLabel("");
    public InputField loginNameField = new InputField(200, 25, "Enter username here");
    JLabel loginPasswordLabel = new JLabel("Password:");
    public JLabel loginPasswordErrorLabel = new JLabel("");
    public InputPasswordField loginPasswordField = new InputPasswordField(200, 25, "Enter password here");
    InputButton loginButton = new InputButton(60, 25) {
        @Override
        public void onClick() {
            loginHandler.login();
        }
    }.setText("Login")
            .setAntialiased(true)
            .setCurve(15)
            .setBg(Color.decode("#38A1F3"))
            .setHoverColor(Color.decode("#38A1F3").darker())
            .setFg(Color.WHITE);

    InputButton findNameButton = new InputButton(60, 25) {
        @Override
        public void onClick() {
            String email = (String) JOptionPane.showInputDialog(null, "Enter your email", "Forgot Username", JOptionPane.PLAIN_MESSAGE);
            if (User.hasUserByEmail(email)) {
                JOptionPane.showMessageDialog(null, "Your email is linked to the username \"" + User.getUserByEmail(email).getUsername() + "\"", "Forgot Username", JOptionPane.INFORMATION_MESSAGE);
            } else if (email != null) {
                JOptionPane.showMessageDialog(null, "No account is linked to this email", "Forgot Username", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }.setText("Forgot Username")
            .setAntialiased(true)
            .setCurve(15)
            .setBg(Color.decode("#38A1F3"))
            .setHoverColor(Color.decode("#38A1F3").darker())
            .setFg(Color.WHITE);

    InputButton resetButton = new InputButton(60, 25) {
        @Override
        public void onClick() {
            String username = loginNameField.getText();
            //if the name in the login field exists, reset that password. If not, prompt the user to enter their username and then try to reset it
            if (User.hasUserByName(username)) {
                User.resetPassword(User.getUserByName(username));
            } else {
                username = (String) JOptionPane.showInputDialog(null, "Enter your username", "Forgot Password", JOptionPane.PLAIN_MESSAGE, null, null, username);
                if (User.hasUserByName(username)) {
                    User.resetPassword(User.getUserByName(username));
                } else if (username != null) {
                    JOptionPane.showMessageDialog(null, "No user found by that name.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }.setText("Forgot Password")
            .setAntialiased(true)
            .setCurve(15)
            .setBg(Color.decode("#38A1F3"))
            .setHoverColor(Color.decode("#38A1F3").darker())
            .setFg(Color.WHITE);

    //register components
    JLabel registerNameLabel = new JLabel("Enter your username:");
    public JLabel registerNameErrorLabel = new JLabel("");
    public InputField registerNameField = new InputField(200, 25, "Enter username here");

    JLabel registerEmailLabel = new JLabel("Enter your email:");
    public JLabel registerEmailErrorLabel = new JLabel("");
    public InputField registerEmailField = new InputField(200, 25, "Enter email here");

    JLabel registerPasswordLabel = new JLabel("Enter your password:");
    public JLabel registerPasswordErrorLabel = new JLabel("");
    public InputPasswordField registerPasswordField = new InputPasswordField(200, 25, "Enter password here");

    JLabel registerFirstNameLabel = new JLabel("Enter your first name:");
    public JLabel registerFirstNameErrorLabel = new JLabel("");
    //add restriction to prevent spaces. TODO: rewrite InputField to add a "alphanumeric only" option instead
    public InputField registerFirstNameField = new InputField(200, 25, "Enter first name here").addRestriction(' ', 0);

    JLabel registerLastNameLabel = new JLabel("Enter your last name:");
    public JLabel registerLastNameErrorLabel = new JLabel("");
    //add restriction to prevent spaces. TODO: rewrite InputField to add a "alphanumeric only" option instead
    public InputField registerLastNameField = new InputField(200, 25, "Enter last name here").addRestriction(' ', 0);
    ;
    InputButton registerButton = new InputButton(60, 25) {
        @Override
        public void onClick() {
            registerHandler.register();

        }
    }.setText("Register")
            .setAntialiased(true)
            .setCurve(15)
            .setBg(Color.decode("#38A1F3"))
            .setHoverColor(Color.decode("#38A1F3").darker())
            .setFg(Color.WHITE);

    /**
     * Default constructor
     */
    public GUI() {
        //init the GUI from various methods
        init();
        initLayout();
        initLogin();
        initRegister();
        initComponents();
    }

    private void init() {
        //set the size of the GUI
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        this.setResizable(false);
        //center the GUI
        this.setLocationRelativeTo(null);

        //ask for confirmation when closing
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                } else {

                }
            }

        });
    }

    private void initLayout() {
        //set the LayoutManager
        this.setLayout(gbl);
        gbl.columnWeights = new double[]{1};
        gbl.rowWeights = new double[]{1};
    }

    /**
     * private void initLogin() { //configure the loginPanel and its components
     * loginPanel.setSize(this.getSize()); GridBagLayout loginLayout = new
     * GridBagLayout(); loginPanel.setLayout(loginLayout); GridBagConstraints
     * loginConstraints = new GridBagConstraints(); loginConstraints.insets =
     * new Insets(5, 0, 0, 10); Font errorFont = new Font("Dialog", (Font.BOLD |
     * Font.ITALIC), 9);
     *
     * loginConstraints.gridx = 0; loginConstraints.gridy = 0; JLabel header =
     * new JLabel("Login"); header.setFont(new Font("Dialog", (Font.BOLD), 25));
     * loginPanel.add(header, loginConstraints); loginConstraints.gridy = 1;
     * loginConstraints.gridx = 2;
     *
     * loginNameErrorLabel.setFont(errorFont);
     * loginNameErrorLabel.setForeground(Color.RED);
     * loginPanel.add(loginNameErrorLabel, loginConstraints);
     *
     * loginConstraints.gridx = 4;
     *
     * loginPasswordErrorLabel.setFont(errorFont);
     * loginPasswordErrorLabel.setForeground(Color.RED);
     * loginPanel.add(loginPasswordErrorLabel, loginConstraints);
     *
     * loginConstraints.gridx = 1; loginConstraints.gridy = 2;
     * loginPanel.add(loginNameLabel, loginConstraints);
     * loginConstraints.gridx++; loginPanel.add(loginNameField,
     * loginConstraints);
     *
     * loginConstraints.gridx++; loginPanel.add(loginPasswordLabel,
     * loginConstraints); loginConstraints.gridx++;
     * loginPanel.add(loginPasswordField, loginConstraints);
     * loginConstraints.gridy = 3; loginConstraints.gridx = 2;
     * loginPanel.add(findNameButton, loginConstraints); loginConstraints.gridx
     * = 4; loginPanel.add(resetButton, loginConstraints);
     * loginConstraints.gridy = 4; loginConstraints.gridx = 3;
     * loginPanel.add(loginButton, loginConstraints); }
     */
    private void initLogin() {
        //configure the loginPanel and its components
        loginPanel.setSize(this.getSize());
        GridBagLayout loginLayout = new GridBagLayout();
        loginPanel.setLayout(loginLayout);
        GridBagConstraints loginConstraints = new GridBagConstraints();
        loginConstraints.insets = new Insets(5, 0, 0, 10);
        Font errorFont = new Font("Dialog", (Font.BOLD | Font.ITALIC), 9);

        loginConstraints.gridx = 2;
        loginConstraints.gridy = 0;
        JLabel header = new JLabel("Login");
        header.setFont(new Font("Dialog", (Font.BOLD), 25));
        loginPanel.add(header, loginConstraints);
        loginConstraints.gridy = 1;
        loginConstraints.gridx = 1;

        loginNameErrorLabel.setFont(errorFont);
        loginNameErrorLabel.setForeground(Color.RED);
        loginPanel.add(loginNameErrorLabel, loginConstraints);

        loginConstraints.gridx = 3;

        loginPasswordErrorLabel.setFont(errorFont);
        loginPasswordErrorLabel.setForeground(Color.RED);
        loginPanel.add(loginPasswordErrorLabel, loginConstraints);

        loginConstraints.gridx = 0;
        loginConstraints.gridy = 2;
        loginPanel.add(loginNameLabel, loginConstraints);
        loginConstraints.gridx++;
        loginPanel.add(loginNameField, loginConstraints);

        loginConstraints.gridx++;
        loginPanel.add(loginPasswordLabel, loginConstraints);
        loginConstraints.gridx++;
        loginPanel.add(loginPasswordField, loginConstraints);
        loginConstraints.gridy = 3;
        loginConstraints.gridx = 1;
        loginPanel.add(findNameButton, loginConstraints);
        loginConstraints.gridx = 3;
        loginPanel.add(resetButton, loginConstraints);
        loginConstraints.gridy = 4;
        loginConstraints.gridx = 2;
        loginPanel.add(loginButton, loginConstraints);
    }

    private void initRegister() {
        //configure the registerPanel and its components
        GridBagLayout registerLayout = new GridBagLayout();
        registerPanel.setLayout(registerLayout);
        GridBagConstraints registerConstraints = new GridBagConstraints();

        registerConstraints.insets = new Insets(5, 0, 0, 10);

        Font errorFont = new Font("Dialog", (Font.BOLD | Font.ITALIC), 9);

        registerConstraints.gridx = 2;
        registerConstraints.gridy = 0;
        JLabel header = new JLabel("Register");
        header.setFont(new Font("Dialog", (Font.BOLD), 25));
        registerPanel.add(header, registerConstraints);
        registerConstraints.gridx = 0;
        registerConstraints.gridy = 1;
        registerPanel.add(registerNameLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerNameField, registerConstraints);
        registerConstraints.gridx++;
        registerNameErrorLabel.setForeground(Color.RED);
        registerNameErrorLabel.setFont(errorFont);
        registerPanel.add(registerNameErrorLabel, registerConstraints);

        registerConstraints.gridy++;
        registerConstraints.gridx = 0;
        registerPanel.add(registerEmailLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerEmailField, registerConstraints);
        registerConstraints.gridx++;
        registerEmailErrorLabel.setForeground(Color.RED);
        registerEmailErrorLabel.setFont(errorFont);
        registerPanel.add(registerEmailErrorLabel, registerConstraints);

        registerConstraints.gridy++;
        registerConstraints.gridx = 0;
        registerPanel.add(registerPasswordLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerPasswordField, registerConstraints);
        registerConstraints.gridx++;
        registerPasswordErrorLabel.setForeground(Color.RED);
        registerPasswordErrorLabel.setFont(errorFont);
        registerPanel.add(registerPasswordErrorLabel, registerConstraints);

        registerConstraints.gridy++;
        registerConstraints.gridx = 0;
        registerPanel.add(registerFirstNameLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerFirstNameField, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerLastNameLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerLastNameField, registerConstraints);

        registerConstraints.gridx = 4;
        registerConstraints.gridy++;
        registerPanel.add(registerButton, registerConstraints);
    }

    private void initComponents() {
        //add the components to the GUI
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(loginPanel, gbc);
        gbc.gridy++;
        this.add(registerPanel, gbc);
    }
}
