/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simrskhanza;

import fungsi.koneksiDB;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import usu.widget.util.WidgetUtilities;

/**
 *
 * @author khanzasoft
 */
public class SIMRSKhanza {

    private static final Properties prop = new Properties(); //edit-novan untuk auto update
    public static String version; //edit-novan untuk auto update
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));

        } catch (Exception e) {
            System.out.println("Notif Setting : "+e);
        }
        version = prop.getProperty("VERSION");
        if(prop.getProperty("AUTOUPDATESISTEM").equals("yes")){
            try {
                if (!Update.getLatestVersion().equals(version) ) {
                    new UpdateInfo(Update.getWhatsNew());
                } else {
                    if(koneksiDB.condb() == null){
                        WidgetUtilities.invokeLater(() -> {
                           JOptionPane.showMessageDialog(null, "Koneksi Putus");
                        });                             
                    } else {
                        WidgetUtilities.invokeLater(() -> {
                           splash utama=new splash();
                           utama.setVisible(true);
                        });                                                         
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if(koneksiDB.condb() == null){
                JOptionPane.showMessageDialog(null, "Koneksi Putus");
            } else {
                WidgetUtilities.invokeLater(() -> {
                   splash utama=new splash();
                   utama.setVisible(true);
                });                             
            }   
        }
    } //edit-novan untuk auto update
    
}
