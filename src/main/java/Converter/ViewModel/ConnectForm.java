package Converter.ViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ConnectForm extends JFrame {
    private JComboBox DbCombo;
    private JTextField textFieldAdress;
    private JTextField textFieldPort;
    private JButton ConnectButton;
    private JPanel rootPanel;

    public ConnectForm() throws HeadlessException {
        super("ConnectForm");
        setContentPane(rootPanel);
        InitDbCombo();
        InitButton();
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void InitDbCombo() {

        DbCombo.addItem(NoSQLTypes.MongoDB);
        DbCombo.addItem(NoSQLTypes.Neo4J);
        DbCombo.addItemListener(new ItemChangeListener());
        textFieldAdress.setText("localhost");
        textFieldPort.setText(NoSQLTypes.MongoDB.defPort);

    }

    public void InitButton() {

        ConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoSQLTypes item = (NoSQLTypes) DbCombo.getSelectedItem();
                // FormManager.statusDialog();
                if (item.defPort != textFieldPort.getText()) {

                }

                FormManager.connect(item.operations);

            }
        });

    }

    class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {

            if (event.getStateChange() == ItemEvent.SELECTED) {
                NoSQLTypes item = (NoSQLTypes) event.getItem();
                textFieldPort.setText(item.defPort);
            }
        }
    }


}
