package Converter.ViewModel;

import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CouchDBForm extends JFrame {

    private final DocumentDataBaseOperations DocumentOperaions;
    private JTextField bucketName;
    private JButton loadButton;
    private JPanel panel;

    public CouchDBForm(DocumentTypesDB documentTypesDB) {

        super("MigrationForm");
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        this.DocumentOperaions = documentTypesDB.getOperations();
        InitListener();
    }


    public void InitListener() {

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DocumentOperaions.loadIntoMemory(bucketName.getText());
            }
        });
    }
}
