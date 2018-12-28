package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.pdfbox.pdfviewer.MapEntry;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Transakcija novca");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

    }
    public static Connection konektuj_se(){
        String url="jdbc:sqlite:C:/SQLite/sqlite-tools-win32-x86-3260000/"+"Baza Transakcije";
        Connection con=null;
        try {
            con=DriverManager.getConnection(url);
            Statement st=con.createStatement();
            st.close();
        } catch (SQLException e) {
            System.out.println("Greska:"+e.getMessage());
        }
        return con;
    }
    public void dodaj(String Ime, String Prezime,String Banka,String Racun,String Stanje){
        Connection con=this.konektuj_se();
        String query;
        try {
            Statement st=con.createStatement();
            query="INSERT INTO 'Tabela Transakcije' VALUES('"+Ime+"','"+Prezime+"','"+Banka+"','"+Racun+"','"+Stanje+"')";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void unisti(String naziv_tabele) throws SQLException {
        Connection con=this.konektuj_se();
        Statement st=con.createStatement();
        String query="DROP TABLE '"+naziv_tabele+"'";
        st.executeUpdate(query);
        query="CREATE TABLE '"+naziv_tabele+"'(Ime TEXT,Prezime TEXT,'Naziv banke' TEXT,'Broj Racuna' TEXT,Stanje TEXT)";
        st.executeUpdate(query);
        st.close();
    }

    public static void main(String[] args) throws SQLException {
        launch(args);

         Main a=new Main();
         a.unisti("Tabela Transakcije");
        try {
            Workbook workbook = null; {
                try {
                    workbook = WorkbookFactory.create(new File("./Izlaz.xls"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*
             * UCITAVAM PODATKE IZ EKSELA
             * */
            Sheet lista=workbook.getSheetAt(0);
            Iterator<Row> red=lista.rowIterator();

            String ime = null,prezime= null  ,brojRacuna= null ,banka = null;
            String stanje=null;
            int  i=0,j=0;
            while(red.hasNext())
            {
                Row redUnutar=red.next();
                Iterator<Cell> celija=redUnutar.cellIterator();
                while(celija.hasNext() && i!=0)
                {
                    ime=celija.next().toString();
                    prezime=celija.next().toString();
                    brojRacuna=celija.next().toString();
                    banka=celija.next().toString();
                    stanje=celija.next().toString();
                    a.dodaj(ime,prezime,brojRacuna,banka,stanje);

                    j++;
                }
                i++;

            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }

