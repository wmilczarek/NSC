package Converter.ViewModel;

import Converter.Controller.DB.NoSQL.NoSQLDataBaseOperations;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

/**
 * Created by szef on 2014-05-16.
 */
public class MigrationForm extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JList dbList;
    private JList entitiList;
    private JButton loadButton;
    private JButton reviewEntity;
//    private JList fieldList;
    private JList typeList;
    private JLabel types;
    private JTable fieldTable;

    private NoSQLDataBaseOperations noSqlOperaions;
    private ListModel<String> entityListModel;


    public MigrationForm(NoSQLDataBaseOperations operations) throws HeadlessException {

        super("MigrationForm");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        this.noSqlOperaions = operations;

        /*entityListModel = new
        entitiList = new JList(entityListModel);
        */
        populateList(dbList, noSqlOperaions.GetDataBaseNames());
        InitListener();
    }

    public void populateTable(JTable table, java.util.List<java.util.List<String>> source) {

        Vector<String> columnNames = new Vector<String>();
        Vector<Object> data = new Vector<Object>();
        Vector<String> row;
        columnNames.addElement("Field Name");
        columnNames.addElement("Field SQL Type");
        columnNames.addElement("SQL Relations");

        for(List<String> list:source){
            row = new Vector<String>();
            row.addElement(list.get(0));
            row.addElement(list.get(1));
            row.addElement(list.get(2));
            data.addElement(row);
        }







        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        table.setModel(model);
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
                try {
                    populateList(entitiList, noSqlOperaions.loadDataBase((String) dbList.getSelectedValue()));
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            }
        });

        entitiList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                try {
                    populateTable(fieldTable, noSqlOperaions.showFields(dbList.getSelectedValue().toString(), entitiList.getSelectedValue().toString()));
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }


            }

        });

/*        fieldList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                    populateList(typeList, noSqlOperaions.showTypes(dbList.getSelectedValue().toString(), entitiList.getSelectedValue().toString()));
            }

        });*/

        reviewEntity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                noSqlOperaions.loadIntoMemory(dbList.getSelectedValue().toString());
            }
        });






/*
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    noSqlOperaions.ResolveAndGetEntites((String) dbList.getSelectedValue());
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            }
        });
*/

    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}



