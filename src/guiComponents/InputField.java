/*
 * © 2019 Daniel Allen
 */
package guiComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * JTextField with convenient customization options.<br><br>
 *
 * @author Daniel Allen
 */
public class InputField extends JTextField {

    //store the input restriction booleans.
    private boolean numOnly = false;
    private boolean allowDecimals = true;
    private CustomDocFilter cdf = new CustomDocFilter();

    //<editor-fold defaultstate="collapsed" desc="Enums">
    enum Alignments {

        HORIZONTAL_LEFT(-3),
        HORIZONTAL_MIDDLE(-2),
        HORIZONTAL_RIGHT(-1),
        VERTICAL_BOTTOM(1),
        VERTICAL_MIDDLE(2),
        VERTICAL_TOP(3);

        final int align;

        Alignments(int align) {
            this.align = align;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new InputField with the specified width, height, and
 tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code>setToolTipText()</code> method.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     * @param tooltip the tooltip that will display when the mouse hovers above
     * the field.
     *
     * @see setToolTipText
     */
    public InputField(int width, int height, String tooltip) {
        this.setPreferredSize(new Dimension(width, height));
        this.setPlaceholder(tooltip);
        init();
    }

    /**
     * Creates a new InputField with the specified width and height.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     *
     */
    public InputField(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        init();
    }

    /**
     * Creates a new InputField with the specified tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code>setToolTipText()</code> method.
     *
     * @param tooltip the tooltip that will display when the mouse hovers above
     * the field.
     *
     * @see setToolTipText
     */
    public InputField(String tooltip) {
        this.setPlaceholder(tooltip);
        init();
    }

    /**
     * Creates a new InputField with no predetermined values.
     *
     */
    public InputField() {
        init();
    }

    //initialize the component
    private void init() {
        setOpaque(false);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                scrollingPlaceholder = true;
                // cancelled = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                scrollingPlaceholder = false;
            }
        });
        ((PlainDocument) this.getDocument()).setDocumentFilter(cdf);
        this.enableInputMethods(false);
        this.setEchoChar((char)0);
    }
    private char echoChar = '\u0000';
    public void setEchoChar(char c){
        this.echoChar = c;
        repaint();
        revalidate();
    }

//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Padding">
    //padding methods and storage
    private Border padding = BorderFactory.createEmptyBorder();

    /**
     * Set the padding of the insides of the InputField.<br>
     * This changes the position of the text inside the field.
     *
     * @param top The padding at the top of the field.
     * @param left The padding at the left of the field.
     * @param bottom The padding at the bottom of the field.
     * @param right The padding at the right of the field.
     * @return
     */
    public InputField setPadding(int top, int left, int bottom, int right) {
        padding = BorderFactory.createEmptyBorder(top, left, bottom, right);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
        return this;
    }

    /**
     * Get the border representing this field's padding.
     *
     * @return An Empty Border filled with the current padding.
     */
    public Border getPadding() {
        return padding;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Text input options">
    /**
     * Setter method to set whether the user can type only numbers or not.
     *
     * @param numOnly true to only allow numbers | false to allow anything.
     * @return
     */
    public InputField setNumbersOnly(boolean numOnly) {
        this.numOnly = numOnly;
        cdf.setRegex("[0-9]+$");
        return this;
    }

    /**
     * Getter method to get whether the field can receive text input or numbers
     * only.
     *
     * @return whether this field can only use numbers.
     */
    public boolean isNumbersOnly() {
        return this.numOnly;
    }

    /**
     *
     * @param regex
     * @return
     */
    public InputField setRegex(String regex) {
        this.allowDecimals = true;
        this.numOnly = false;
        cdf.setRegex(regex);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Decimal Input Options">
    /**
     * Allow or disallow decimals in the field.
     *
     * @param dec True to allow decimals | False to disable them.
     * @return
     * @throws IllegalStateException if this attempts to set allowDecimals to
     * false before numbersOnly is true.
     * @see setNumbersOnly
     */
    public InputField setAllowDecimals(boolean dec) throws IllegalStateException {
        if (!isNumbersOnly() && !dec) {
            throw new IllegalStateException("Must set to numbers only before denying decimals!");
        } else if (dec == true) {
            cdf.setRegex("[0-9.?]+$");
            addRestriction('.', 1);

        } else {
            cdf.setRegex("[0-9]+$");
        }
        this.allowDecimals = dec;
        return this;
    }

    /**
     * Getter method to get whether this field will accept decimals.
     *
     * @return
     */
    public boolean getAllowsDecimals() {
        return this.allowDecimals;
    }

    /**
     * Adds a character restriction.
     *
     * @param c The character
     * @param limit The maximum number of the character specified.
     * @return this InputField
     */
    public InputField addRestriction(char c, int limit) {
        cdf.addRestriction(c, limit);
        return this;
    }

    /**
     * Removes a character restriction.
     *
     * @param c The character
     * @return true if the character limit was successfully removed.
     */
    public boolean removeRestriction(char c) {
        return cdf.removeRestriction(c);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Number Limits">
    private Double min = null;
    private Double max = null;

    /**
     * The minimum value this field can hold. A null value will disable any
     * limit.
     *
     * @param min The minimum value this field can hold.
     * @return
     */
    public InputField setMinimum(Double min) {
        this.min = min;
        return this;
    }

    /**
     * The maximum value this field can hold. A null value will disable any
     * limit.
     *
     * @param max The maximum value this field can hold.
     * @return
     */
    public InputField setMaximum(Double max) {
        this.max = max;
        return this;
    }

    /**
     * Gets the minimum value this field can hold.
     *
     * @return the minimum value this field can hold.
     */
    public Double getMinimum() {
        return this.min;
    }

    /**
     * Gets the maximum value this field can hold.
     *
     * @return the maximum value this field can hold.
     */
    public Double getMaximum() {
        return this.max;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Rounded Corners">
    private int curve = 0;

    /**
     * Setter method to set the curve of the corners of the field.
     *
     * @param curve The radius of the curve
     * @return
     */
    public InputField setCurve(int curve) {
        this.curve = curve;
        repaint();
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Placeholder Text">
    private String placeholder = "";

    Timer ses;
    private boolean scrollingPlaceholder = false;

    /**
     *
     * @param scroll
     * @return
     */
    public InputField setScrollingPlaceholder(boolean scroll) {
        this.scrollingPlaceholder = scroll;
        setPlaceholder(placeholder);
        return this;
    }

    private static double maxScreenHertz = 60;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        for (GraphicsDevice gd : gds) {
            DisplayMode dm = gd.getDisplayMode();
            maxScreenHertz = Math.max(dm.getRefreshRate(), maxScreenHertz);
            //System.out.println("Device id: \"" + gd.getIDstring() + "\" - Refresh rate: " + dm.getRefreshRate() + "hz");
        }
    }

    private long scrollDelay = (long) (1000 / maxScreenHertz);
    private double scrollSpeed = 1;

    /**
     *
     * @param speed
     * @return
     */
    public InputField setScrollDelay(long delay) {
        this.scrollDelay = delay;
        setPlaceholder(placeholder);
        return this;
    }

    public InputField setScrollSpeed(double speed) {
        this.scrollSpeed = speed;
        setPlaceholder(placeholder);
        return this;
    }
    private boolean cancelRequested = false;
    private boolean cancelled = false;

    /**
     * Setter method to set the place text of the field. This only displays if
     * the field has no input, and the place is not empty.
     *
     * @param place The text to display.
     * @return
     * @see getPlaceholder
     */
    public InputField setPlaceholder(final String place) {
        this.placeholder = place;
        if (ses == null) {
            ses = new Timer();
        }

        //if (((Graphics2D) buffer.getGraphics()).getFontMetrics().stringWidth(this.place) > getWidth()) {//&& buffer.getWidth() == getWidth() && buffer.getHeight() == getHeight()) {
        ses.schedule(new TimerTask() {
            @Override
            public void run() {
                cancelRequested = !scrollingPlaceholder;
                if (!cancelRequested) {
                    cancelled = false;
                }
                repaint();

                if (currentPlaceholderPhaseShift > buffer.getGraphics().getFontMetrics().stringWidth(place) - getWidth() + getBorderInsets().right + 5) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println("Error");
                    }
                    currentPlaceholderPhaseShift = 0;
                    cancelled = !scrollingPlaceholder;
                    repaint();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println("Error");
                    }
                }

                if (buffer.getGraphics().getFontMetrics().stringWidth(place) > getWidth() && (!cancelled || currentPlaceholderPhaseShift != 0)) {
                    //System.out.println("Scroll speed: " + scrollSpeed + " - Phase Shift: " + currentPlaceholderPhaseShift);
                    currentPlaceholderPhaseShift += scrollSpeed / scrollDelay;
                    cancelled = false;
                }

            }
        }, 1000, scrollDelay);
        //  }

        return this;
    }

    public Insets getBorderInsets() {
        return padding.getBorderInsets(this);
    }

    /**
     * Getter method to get the placeholder text of this field.
     *
     * @return The placeholder text.
     * @see setPlaceholder
     */
    public String getPlaceholder() {
        return this.placeholder;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Painting">
    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    /**
     * Paints the component. This should not be called manually, and should
     * instead use <code>repaint()</code>
     *
     * @param g
     * @see repaint()
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (buffer.getWidth() != this.getWidth() || buffer.getHeight() != this.getHeight()) {
            buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();

        if (antialias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        g2d.setColor(getBackground());
        g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, curve, curve);
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            g2d.setColor(getDisabledTextColor());
            g2d.drawString(placeholder, borderWeight + padding.getBorderInsets(this).left - (int) currentPlaceholderPhaseShift, g.getFontMetrics().getHeight());
            if (g2d.getFontMetrics().stringWidth(placeholder) > getWidth()) {
                g2d.clearRect(-borderWeight, borderWeight + padding.getBorderInsets(this).left, 0, getHeight());
                g2d.clearRect(getWidth() - borderWeight, getWidth() + borderWeight, 0, getHeight());
            }
        } else if (!getText().isEmpty()) {

        }
        g2d.setColor(borderColor);
        g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, curve, curve);

        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }
    double currentPlaceholderPhaseShift = 0;

    /**
     * This should not be called manually. Use <code>repaint()</code> instead.
     *
     * @param g
     * @see repaint()
     */
    @Override
    protected void paintBorder(Graphics g) {

    }

    private Shape fieldShape;
    private int knownCurve = curve;
//</editor-fold>
    private boolean antialias = false;

    /**
     *
     * @param alias
     * @return
     */
    public InputField setAntialiased(boolean alias) {
        this.antialias = alias;
        return this;
    }

    /**
     *
     * @return
     */
    public boolean isAntialiased() {
        return this.antialias;
    }

    /**
     *
     * @param c
     * @return
     */
    public InputField setDisabledColor(Color c) {
        super.setDisabledTextColor(c);
        return this;
    }

    /**
     *
     * @param c
     * @return
     */
    public InputField setEnabledColor(Color c) {
        super.setForeground(c);
        return this;
    }

    /**
     *
     * @param c
     * @return
     */
    public InputField setBackgroundColor(Color c) {
        super.setBackground(c);
        return this;
    }

    /**
     *
     * @param bordCol
     * @return
     */
    public InputField setBorderColor(Color bordCol) {
        this.borderColor = bordCol;
        return this;
    }

    /**
     *
     * @param weight
     * @return
     */
    public InputField setBorderWeight(int weight) {
        this.borderWeight = weight;
        return this;
    }
    private int borderWeight = 1;
    private Color borderColor = Color.BLACK;

    /**
     * Detect if this field contains a point in it. This is adapted to account
     * for rounded corners.
     *
     * @param x The X-coordinate of the point
     * @param y The Y-coordinate of the point
     * @return
     */
    @Override
    public boolean contains(int x, int y) {
        if (fieldShape == null || !fieldShape.getBounds().equals(getBounds()) || knownCurve != curve) {
            knownCurve = curve;
            fieldShape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        }
        return fieldShape.contains(x, y);

    }

    //custom document filter to restrict what can be typed into the field.
    class CustomDocFilter extends DocumentFilter {

        private HashMap<Character, Integer> numRestrictions = new HashMap<>();

        private String regex = "";

        public CustomDocFilter() {
            super();
        }

        public CustomDocFilter(String reg) {
            super();
            this.regex = reg;
        }

        public CustomDocFilter setRegex(String reg) {
            this.regex = reg;
            return this;
        }

        public boolean addRestriction(Character c, Integer limit) {
            if (numRestrictions.containsKey(c)) {
                return false;
            }
            this.numRestrictions.put(c, limit);
            return true;
        }

        public boolean removeRestriction(Character c) {
            if (!numRestrictions.containsKey(c)) {
                return false;
            }
            this.numRestrictions.remove(c);
            return true;
        }

        public int getMaxOf(Character c) {
            return numRestrictions.get(c);
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offs, String str, AttributeSet as) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offs, str);
            if (canAppend(sb.toString()) && (!sb.toString().startsWith(".") || sb.toString().length() > 1)) {
                boolean allowed = true;
                for (char c : str.toCharArray()) {
                    if (numRestrictions.containsKey(c)) {
                        if (countMatches(sb.toString(), c) > numRestrictions.get(c)) {
                            allowed = false;
                        }
                    }
                }
                if (allowed) {
                    super.insertString(fb, offs, str, as);
                }
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offs, int length, String str, AttributeSet as) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offs, str);
            if (canAppend(sb.toString()) && (!sb.toString().startsWith(".") || sb.toString().length() > 1)) {
                boolean allowed = true;
                for (char c : str.toCharArray()) {
                    if (numRestrictions.containsKey(c)) {
                        if (countMatches(sb.toString(), c) > numRestrictions.get(c)) {
                            allowed = false;
                        }
                    }
                }
                if (allowed) {
                    super.replace(fb, offs, length, str, as);
                }
            }
        }

        public int countMatches(String str, char ch) {
            if (str == null || str.isEmpty()) {
                return 0;
            }
            int count = 0;
            for (char c : str.toCharArray()) {
                if (c == ch) {
                    count++;
                }
            }
            return count;
        }

        private boolean canAppend(String input) {
            return !(!input.matches(regex) && !regex.isEmpty());
        }
    }
}
