import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class game24 extends JFrame implements ActionListener {
    static final int GAME_WIDTH = 1000;
	static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    
    private static JTextField textField;
    private JButton newButton;
    private JButton checkButton;
    private static JLabel questionLabel;
    private static JPanel panel;

    public game24(List<Integer> numbers) {
        
        setTitle("Game 24");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_SIZE);
        setLocationRelativeTo(null);
        
        textField = new JTextField(30);
        
        questionLabel = new JLabel();
        StringBuilder sb = new StringBuilder();
        for (int num : numbers) {
            sb.append(num).append(" ");
        }
        questionLabel.setText(sb.toString().trim());
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        newButton = new JButton("New");
        newButton.addActionListener(this);
        
        checkButton = new JButton("Check");
        checkButton.addActionListener(this);
        
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0); // top, left, bottom, right
        panel.add(questionLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0); // top, left, bottom, right
        panel.add(textField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 10, 0); // top, left, bottom, right
        panel.add(checkButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0); // top, left, bottom, right
        panel.add(newButton, gbc);
        
        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);

    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// check which button was clicked
        if (e.getSource() == checkButton) {
            System.out.println("Button check was clicked");
            String message = textField.getText();
            Client.checkButtonActive(message);
        } else if (e.getSource() == newButton) {
            System.out.println("Button 2 was clicked");
            String message = "request new";
            Client.newButtonActive(message);
        }
    }

    public static void updateNewNumbers(List<Integer> numbers) {
        System.out.println("Set new question");
        StringBuilder nsb = new StringBuilder();
        for (int num : numbers) {
            nsb.append(num).append(" ");
        }
        questionLabel.setText(nsb.toString().trim());
    }

    public static void popup(int n) {
        if (n == 1) {
            int option = JOptionPane.showConfirmDialog(panel, "Awesome!! \n New question?");
                if (option == JOptionPane.YES_OPTION) {
                    String message = "request new";
                    Client.newButtonActive(message);
                    textField.setText("");
            }
        } else if (n == 2) {
            JOptionPane.showMessageDialog(panel, "Try again!");
        } else if (n == 3) {
            JOptionPane.showMessageDialog(panel, "You cheat!");
        } else if (n == 0) {
            JOptionPane.showMessageDialog(panel, "You parameter wrong!");
        }
    }
}

