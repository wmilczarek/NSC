package Converter.View;

import Converter.ModelController.DocumentTypesDB;

import javax.swing.*;

/**
 * Created by szef on 2014-05-16.
 */
public class FormManager {

    private static ConnectForm connectForm;
    private static MongoDBForm mongoDBForm;
    private static CouchDBForm couchDBForm;

    private FormManager() {
    }

    public static void startup() {
        connectForm = new ConnectForm();
    }

    public static void connect(DocumentTypesDB documentTypesDB) {
        connectForm.setVisible(false);

        if(documentTypesDB == DocumentTypesDB.MongoDB) {
            mongoDBForm = new MongoDBForm(documentTypesDB);
        } else if (documentTypesDB == DocumentTypesDB.CouchDB){
            couchDBForm = new CouchDBForm(documentTypesDB);

        }
    }


    public static void statusConnectFormDialog(String msg) {
        JOptionPane.showMessageDialog(connectForm, msg);
    }


}
