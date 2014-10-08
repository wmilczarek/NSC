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

        DbCombo.addItem(DocumentTypesDB.MongoDB);
        DbCombo.addItem(DocumentTypesDB.CouchDB);
        DbCombo.addItemListener(new ItemChangeListener());
        textFieldPort.setText(String.valueOf(((DocumentTypesDB) DbCombo.getSelectedItem()).getDefPort()));
        textFieldAdress.setText(String.valueOf(((DocumentTypesDB) DbCombo.getSelectedItem()).getHost()));
    }

    public void InitButton() {

        ConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DocumentTypesDB item = (DocumentTypesDB) DbCombo.getSelectedItem();
                item.setDefPort(Integer.valueOf(textFieldPort.getText()));
                item.setAddress(textFieldAdress.getText());
                FormManager.connect(item);
            }
        });

    }

    class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {

            if (event.getStateChange() == ItemEvent.SELECTED) {
                DocumentTypesDB item = (DocumentTypesDB) event.getItem();
                textFieldPort.setText(String.valueOf(item.getDefPort()));
                textFieldAdress.setText(String.valueOf(item.getHost()));
            }
        }
    }


}
