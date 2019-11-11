/*
 * © 2019 Daniel Allen
 */
package guiComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Button with convenient customization options.<br><br>
 *
 * @author Daniel Allen
 */
public abstract class InputButton extends JComponent {

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
     * tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code>setToolTipText()</code> method.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     * @param text the tooltip that will display when the mouse hovers above the
     * field.
     *
     * @see setToolTipText
     */
    public InputButton(int width, int height, String text) {
        this.setPreferredSize(new Dimension(width, height));
        this.setText(text);
        init();
    }

    /**
     * Creates a new InputField with the specified width and height.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     *
     */
    public InputButton(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        init();
    }

    /**
     * Creates a new InputField with the specified tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code>setToolTipText()</code> method.
     *
     * @param text the tooltip that will display when the mouse hovers above the
     * field.
     *
     * @see setToolTipText
     */
    public InputButton(String text) {
        this.setText(text);
        init();
    }

    /**
     * Creates a new InputField with no predetermined values.
     *
     */
    public InputButton() {
        init();
    }

    //initialize the component
    private void init() {
        setOpaque(false);
        //this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setPreferredSize(new Dimension(
                this.getWidth() / 2 - this.getFontMetrics(this.font).stringWidth(this.text) / 2 + this.paddingX,
                (this.getHeight() - this.getFontMetrics(this.font).getHeight()) / 2 + this.getFontMetrics(this.font).getAscent() + this.paddingY
        ));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bgColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bgColor = getBackground();
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent me) {
                onClick();
            }
        });

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
    public InputButton setPadding(int top, int left, int bottom, int right) {
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
    //<editor-fold defaultstate="collapsed" desc="Rounded Corners">
    private int curve = 0;

    /**
     * Setter method to set the curve of the corners of the field.
     *
     * @param curve The radius of the curve
     * @return
     */
    public InputButton setCurve(int curve) {
        this.curve = curve;
        repaint();
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Painting">
    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private Font font = new Font("Arial Rounded", Font.BOLD, 11);
    int paddingX = 20;
    int paddingY = 10;
    int displaceY = 0;

    @Override
    public Font getFont() {
        return this.font;
    }

    @Override
    public void setFont(Font f) {
        this.font = f;
        this.setPreferredSize(new Dimension(this.getFontMetrics(this.font).stringWidth(text) + this.paddingX, this.getFontMetrics(this.font).getAscent() + this.paddingY));
        repaint();
    }

    /**
     * Paints the component. This should not be called manually, and should
     * instead use <code>repaint()</code>
     *
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

        g2d.setColor(bgColor);
        g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, curve, curve);
        g2d.setColor(getForeground());
        g2d.setFont(font);
        g2d.drawString(this.text,
                this.getWidth() / 2 - g2d.getFontMetrics().stringWidth(this.text) / 2,
                (this.getHeight() - g2d.getFontMetrics().getHeight()) / 2 + g2d.getFontMetrics(this.font).getAscent() + this.displaceY);
        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }

    /**
     * This should not be called manually. Use <code>repaint()</code> instead.
     *
     * @see repaint()
     */
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();

        if (antialias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        g2d.setColor(borderColor);
        g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, curve, curve);
        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }

    /**
     *
     * @param bg
     * @return this
     */
    public InputButton setBg(Color bg) {
        this.setBackground(bg);
        bgColor = bg;
        return this;
    }

    /**
     *
     * @param fg
     * @return this
     */
    public InputButton setFg(Color fg) {
        this.setForeground(fg);
        return this;
    }

    /**
     *
     * @param bordCol
     * @return this
     */
    public InputButton setBorderColor(Color bordCol) {
        this.borderColor = bordCol;
        return this;
    }

    /**
     *
     * @param weight
     * @return
     */
    public InputButton setBorderWeight(int weight) {
        this.borderWeight = weight;
        return this;
    }

    private boolean antialias = false;

    /**
     *
     * @param alias
     * @return
     */
    public InputButton setAntialiased(boolean alias) {
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
    private Color borderColor = Color.BLACK;
    private int borderWeight = 1;
    private Shape fieldShape;
    private int knownCurve = curve;
//</editor-fold>

    /**
     * Detect if this field contains a point in it. This is adapted to account
     * for rounded corners.
     *
     * @param x The X-coordinate of the point
     * @param y The Y-coordinate of the point
     * @return true if this field contains the point
     */
    @Override
    public boolean contains(int x, int y) {
        if (fieldShape == null || !fieldShape.getBounds().equals(getBounds()) || knownCurve != curve) {
            knownCurve = curve;
            fieldShape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        }
        return fieldShape.contains(x, y);
    }
    private Color hoverColor;
    private Color bgColor;

    /**
     *
     * @param h
     * @return
     */
    public InputButton setHoverColor(Color h) {
        this.hoverColor = h;
        return this;
    }

    /**
     *
     * @return
     */
    public Color getHoverColor() {
        return this.hoverColor;
    }

    /**
     *
     */
    public abstract void onClick();

    private String text = "Button";

    /**
     *
     * @return
     */
    public String getText() {
        return this.text;
    }

    /**
     *
     * @param text
     * @return
     */
    public InputButton setText(String text) {
        this.text = text;
        this.setPreferredSize(new Dimension(this.getFontMetrics(this.font).stringWidth(text) + this.paddingX, this.getFontMetrics(this.font).getAscent() + this.paddingY));
        return this;
    }

    /**
     *
     * @param propertyname
     * @param value
     * @return
     */
    public boolean setProperty(String propertyname, Object value) {
        try {
            Method method = this.getClass().getMethod("set" + propertyname, Object.class);
            if (method.getModifiers() == Modifier.PUBLIC) {
                method.invoke(this, value);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return false;
        }
        return true;
    }
}
