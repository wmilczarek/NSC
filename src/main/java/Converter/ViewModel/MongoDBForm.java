package Converter.ViewModel;

import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;


public class MongoDBForm extends JFrame {
    private JPanel panel1;
    private JList dbList;
    private JButton loadButton;

    private DocumentDataBaseOperations noSqlOperaions;


    public MongoDBForm(DocumentTypesDB documentTypesDB) throws HeadlessException {


        super("MigrationForm");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        this.noSqlOperaions = documentTypesDB.getOperations();

        populateList(dbList, noSqlOperaions.GetDataBaseNames());
        InitListener();
    }


    public void populateList(JList list, java.util.List<String> source) {

        DefaultListModel model = new DefaultListModel();

        for (String str : source) {
            model.addElement(str);
        }

        list.setModel(model);
    }


    public void InitListener() {

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                noSqlOperaions.loadIntoMemory(dbList.getSelectedValue().toString());
            }
        });
    }
}



