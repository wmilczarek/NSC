package Converter.ViewModel;

import Converter.ModelController.Controller.DB.NoSQL.NoSQLDataBaseOperations;

import javax.swing.*;

/**
 * Created by szef on 2014-05-16.
 */
public class FormManager {

    private static ConnectForm connectForm;
    private static MigrationForm migrationForm;

    private FormManager() {
    }

    public static void startup() {
        connectForm = new ConnectForm();
    }

    public static void connect(NoSQLDataBaseOperations noSqlOperations) {
        connectForm.setVisible(false);
        migrationForm = new MigrationForm(noSqlOperations);
    }


    public static void statusConnectFormDialog(String msg) {
        JOptionPane.showMessageDialog(connectForm, msg);
    }


}
