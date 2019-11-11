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

    JLabel loginNameLabel = new JLabel("Username:");
    public JLabel loginNameErrorLabel = new JLabel("");
    public InputField loginNameField = new InputField(100, 25, "Enter username here");
    JLabel loginPasswordLabel = new JLabel("Password:");
    public JLabel loginPasswordErrorLabel = new JLabel("");
    public InputPasswordField loginPasswordField = new InputPasswordField(100, 25, "Enter password here");
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

    InputButton resetButton = new InputButton(60, 25) {
        @Override
        public void onClick() {
            String username = loginNameField.getText();
            if(User.hasUserByName(username)){
                User.resetPassword(User.getUserByName(username));
            } else {
                System.out.println("No user by that name");
            }
        }
    }.setText("Reset Password")
            .setAntialiased(true)
            .setCurve(15)
            .setBg(Color.decode("#38A1F3"))
            .setHoverColor(Color.decode("#38A1F3").darker())
            .setFg(Color.WHITE);

    JLabel registerNameLabel = new JLabel("Enter your username:");
    public JLabel registerNameErrorLabel = new JLabel("");
    public InputField registerNameField = new InputField(100, 25, "Enter username here");
    JLabel registerEmailLabel = new JLabel("Enter your email:");
    public JLabel registerEmailErrorLabel = new JLabel("");
    public InputField registerEmailField = new InputField(100, 25, "Enter email here");
    JLabel registerPasswordLabel = new JLabel("Enter your password:");
    public JLabel registerPasswordErrorLabel = new JLabel("");
    public InputPasswordField registerPasswordField = new InputPasswordField(100, 25, "Enter password here");
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
        init();
        initLayout();
        initLogin();
        initRegister();
        initComponents();
    }

    private void init() {
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
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
        this.setLayout(gbl);
        gbl.columnWeights = new double[]{1};
        gbl.rowWeights = new double[]{1};
        //gbl.columnWidths = new int[]{622, 288};
    }

    private void initLogin() {
        loginPanel.setSize(this.getSize());
        GridBagLayout loginLayout = new GridBagLayout();
        //loginLayout.columnWeights = new double[]{0.2, 0.2, 1, 0.2, 0.2};
        loginPanel.setLayout(loginLayout);
        GridBagConstraints loginConstraints = new GridBagConstraints();
        loginConstraints.insets = new Insets(0, 0, 0, 0);

        Font errorFont = new Font("Dialog", (Font.BOLD | Font.ITALIC), 9);
        loginConstraints.gridy = 0;
        loginConstraints.gridx = 1;
        loginConstraints.insets = new Insets(5, 0, 0, 60);

        loginNameErrorLabel.setFont(errorFont);
        loginNameErrorLabel.setForeground(Color.RED);
        loginPanel.add(loginNameErrorLabel, loginConstraints);

        loginConstraints.insets = new Insets(5, 0, 0, 10);
        loginConstraints.gridx = 3;

        loginPasswordErrorLabel.setFont(errorFont);
        loginPasswordErrorLabel.setForeground(Color.RED);
        loginPanel.add(loginPasswordErrorLabel, loginConstraints);


        loginConstraints.gridx = 0;
        loginConstraints.gridy = 1;
        loginPanel.add(loginNameLabel, loginConstraints);
        loginConstraints.gridx++;
        loginConstraints.insets = new Insets(5, 0, 0, 60);
        loginPanel.add(loginNameField, loginConstraints);

        loginConstraints.gridx++;
        loginConstraints.insets = new Insets(5, 0, 0, 10);
        loginPanel.add(loginPasswordLabel, loginConstraints);
        loginConstraints.gridx++;
        loginPanel.add(loginPasswordField, loginConstraints);
        loginConstraints.gridy++;
        loginConstraints.gridx=1;
        loginPanel.add(resetButton, loginConstraints);
        loginConstraints.gridx=2;
        loginPanel.add(loginButton, loginConstraints);
    }

    private void initRegister() {
        GridBagLayout registerLayout = new GridBagLayout();
        registerPanel.setLayout(registerLayout);
        GridBagConstraints registerConstraints = new GridBagConstraints();

        registerConstraints.insets = new Insets(0, 0, 0, 0);

        registerConstraints.gridx = 1;
        registerConstraints.gridy = 0;

        Font errorFont = new Font("Dialog", (Font.BOLD | Font.ITALIC), 9);

        registerNameErrorLabel.setForeground(Color.RED);
        registerNameErrorLabel.setFont(errorFont);
        registerPanel.add(registerNameErrorLabel, registerConstraints);
        registerConstraints.gridx = 3;
        registerEmailErrorLabel.setForeground(Color.RED);
        registerEmailErrorLabel.setFont(errorFont);
        registerPanel.add(registerEmailErrorLabel, registerConstraints);
        registerConstraints.gridx = 5;
        registerPasswordErrorLabel.setForeground(Color.RED);
        registerPasswordErrorLabel.setFont(errorFont);
        registerPanel.add(registerPasswordErrorLabel, registerConstraints);

        registerConstraints.gridx = 0;
        registerConstraints.gridy++;

        registerConstraints.insets = new Insets(5, 0, 0, 10);
        registerPanel.add(registerNameLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerNameField, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerEmailLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerEmailField, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerPasswordLabel, registerConstraints);
        registerConstraints.gridx++;
        registerPanel.add(registerPasswordField, registerConstraints);
        registerConstraints.gridx = 4;
        registerConstraints.gridy++;
        registerPanel.add(registerButton, registerConstraints);
    }

    private void initComponents() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(loginPanel, gbc);
        // gbc.gridy++;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // this.add(new JSeparator(SwingConstants.HORIZONTAL));
        // gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        this.add(registerPanel, gbc);
    }
}
