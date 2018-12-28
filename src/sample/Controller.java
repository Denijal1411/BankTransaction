package sample;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import scala.reflect.internal.Trees;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import static jdk.nashorn.internal.objects.NativeBoolean.valueOf;

public class Controller  implements Initializable {
    public ComboBox posiljalac;
    public ComboBox primalac;
    public int transakcija,primalacStanje,posiljalacStanje,i=0,j=0,izvrsenaTransakcija;
    public TextField iznos;
    public Button btn;
    public String po,pr;
    public Osoba primalacOsoba,posiljalacOsoba;
    public Map<Integer,Osoba>  mapa;
    public Label racunPosiljalac;
    public Label racunPrimalac;
    Alert msg=new Alert(Alert.AlertType.ERROR);
    Alert uspeh=new Alert(Alert.AlertType.INFORMATION);
    /*
    * KADA SE KLIKNE NA DUGME ONDA SE KUPE PODACI IZ MAPRE, PRETHODNO POKUPOLJENI IZ EXELA,
    * A POTOM SE RADE ODREDJENE FUNKCIONALNOSTI KAKO BI PROGRAM ADEKVATNO RADIO*/
    public void klik(ActionEvent actionEvent) {
            pr = (String) primalac.getValue();
            po = (String) posiljalac.getValue();
            try {
                if (po == null) {
                    msg.setTitle("ERROR");
                    msg.setContentText("Molimo vas izaberite posiljaoca transakcije!");
                    msg.setHeaderText(" ");
                    msg.showAndWait();
                } else if (pr == null) {

                    msg.setTitle("ERROR");
                    msg.setContentText("Molimo vas izaberite primaoca transakcije!");
                    msg.setHeaderText(" ");
                    msg.showAndWait();
                } else if (pr.compareTo(po) == 0) {
                    msg.setTitle("ERROR");
                    msg.setContentText("Greska prilikom izbora korisnika!");
                    msg.setHeaderText("Posiljalac i Primalac moraju biti razliciti korisnici!");
                    pr=null;
                    po=null;
                    msg.showAndWait();
                }
                for (Map.Entry<Integer, Osoba> entry : mapa.entrySet()) {

                    if (entry.getKey() != 0 && (entry.getValue().ime.compareTo(pr.toString()) == 0)) {
                        primalacOsoba = entry.getValue();

                    } else if (entry.getKey() != 0 && (entry.getValue().ime.compareTo(po.toString()) == 0)) {
                        posiljalacOsoba = entry.getValue();
                    }
                }

                transakcija=Integer.parseInt(iznos.getText());
                primalacStanje=Integer.parseInt(primalacOsoba.getStanje().substring(0,primalacOsoba.getStanje().length()-2));
                posiljalacStanje=Integer.parseInt(posiljalacOsoba.getStanje().substring(0,posiljalacOsoba.getStanje().length()-2));

                if(transakcija>posiljalacStanje)
                {
                    msg.setTitle("ERROR");
                    msg.setContentText("Korisnik:"+posiljalacOsoba.getIme()+"\n"+"Nema dovoljno sredstava na racunu.\nMolimo Vas pokusajte opet.");
                    msg.setHeaderText(" ");
                    msg.showAndWait();
                }
                else
                {
                    /*
                    * KADA JE SVE PROSLO KAKO TREBA ONDA SE AZURIRA STANJE U EKSELU, TAKO STO PROMENJENE PODATKE
                    * MENJAMO A ONE NEPROMENJENE OSTAJU ISTI.*/
                    Workbook vb= new HSSFWorkbook();
                    Sheet list=vb.createSheet("Prvi list");
                    Row red;

                    int s=0;
                    {
                        for (Map.Entry<Integer,Osoba> entry : mapa.entrySet())
                        {
                            if(s<i){
                                red=list.createRow(s);
                                for( int p=0;p<j;)
                                {
                                    if(entry.getValue().getIme().compareTo(po)==0)
                                    {
                                        izvrsenaTransakcija=posiljalacStanje-transakcija;
                                        red.createCell(p++).setCellValue(entry.getValue().getIme());
                                        red.createCell(p++).setCellValue(entry.getValue().getPrezime());
                                        red.createCell(p++).setCellValue(entry.getValue().getBanka());
                                        red.createCell(p++).setCellValue(entry.getValue().getBrojRacuna());
                                        red.createCell(p++).setCellValue(izvrsenaTransakcija+".0");
                                    }
                                    else if(entry.getValue().getIme().compareTo(pr)==0)
                                    {
                                        izvrsenaTransakcija=primalacStanje+transakcija;
                                        red.createCell(p++).setCellValue(entry.getValue().getIme());
                                        red.createCell(p++).setCellValue(entry.getValue().getPrezime());
                                        red.createCell(p++).setCellValue(entry.getValue().getBanka());
                                        red.createCell(p++).setCellValue(entry.getValue().getBrojRacuna());
                                        red.createCell(p++).setCellValue(izvrsenaTransakcija+".0");
                                    }
                                    else{
                                        red.createCell(p++).setCellValue(entry.getValue().getIme());
                                        red.createCell(p++).setCellValue(entry.getValue().getPrezime());
                                        red.createCell(p++).setCellValue(entry.getValue().getBanka());
                                        red.createCell(p++).setCellValue(entry.getValue().getBrojRacuna());
                                        red.createCell(p++).setCellValue(entry.getValue().getStanje());
                                    }

                                }
                                s++;
                            }
                        }
                    }
                    OutputStream out=new FileOutputStream("Izlaz.xls");
                    ((HSSFWorkbook) vb).write(out);
                    out.close();
                    //AKO JE SVE PROSLO KAKO TREBA Dobijamo informaciju o tome
                    uspeh.setTitle("Informacija");
                    uspeh.setContentText("Uspesno je izvrsena transakcija novca!");
                    uspeh.setHeaderText(" ");
                    uspeh.showAndWait();

                }


            } catch (NullPointerException e) {
                System.out.println("Doslo je do greske nad koriscenjem aplikacije," +
                        "pokusajte ponovo!");
            }
            catch(NumberFormatException e1)
            {
                msg.setTitle("ERROR");
                msg.setContentText("Unesite ispravnu vrednost u polju za transakcije!");
                msg.setHeaderText("Unesite celobrojne vrednosti");
                msg.showAndWait();
            }catch(Exception e2)
            {
                System.out.println("Molim Vas zatvorite Excel pre Pokretanja programa!");
            }
    }

    public Map<Integer, Osoba> getMapa() {
        return mapa;
    }

    @Override // Ovo je kao main za klasu main dakle prva se izvrsava, cak se prva izvrsava i on main funkcije
    public void initialize(URL location, ResourceBundle resources) {
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
            mapa= new TreeMap<Integer,Osoba>();
            String ime = null,prezime= null  ,brojRacuna= null ,banka = null;
            String stanje=null;
            int brojac=0;
            while(red.hasNext())
            {
                Row redUnutar=red.next();
                Iterator<Cell> celija=redUnutar.cellIterator();
                while(celija.hasNext())
                {
                    ime=celija.next().toString();
                    if(brojac!=0)
                    {
                        posiljalac.getItems().add(ime);
                        primalac.getItems().add(ime);
                    }
                    prezime=celija.next().toString();
                    brojRacuna=celija.next().toString();
                    banka=celija.next().toString();
                    stanje=celija.next().toString();
                    mapa.put(brojac++,new Osoba(ime,prezime,brojRacuna,stanje,banka));
                    j++;
                }
                i++;

        }
        posiljalac.setPromptText("-izaberite osobu-");
        primalac.setPromptText("-izaberite osobu-");
        //OVO JE POMOC IZ KONZOLE KOJA POMAZE KORISNIKU DA MU KAZE KOLIKO ELEMENATA IMATABELA I KOJI SU NJENI clanovi
        for (Map.Entry<Integer,Osoba> entry : mapa.entrySet())
        {

            if(entry.getKey()!=0 )
            {
                System.out.print(entry.getKey()+".");
                System.out.println(entry.getValue().toString());

            }
        }


}
    /*OVA FUNKCIJA VRSI PROMENU ZIRORACUNA KOJI SE NALAZI ISPOD COMBOBOXA
     * U ZAVISNOSTI OD IZBORA UPISUJE SE ODGOVARAJUCI BROJ RACUNA CRVENIM SLOVIMA*/
    public void promena(ActionEvent actionEvent) {
        racunPosiljalac.setVisible(true);
        po = (String) posiljalac.getValue();
        if(po!=null)
        racunPosiljalac.setText("");
        for (Map.Entry<Integer,Osoba> entry : mapa.entrySet())
        {

            if(entry.getKey()!=0 && po.compareTo(entry.getValue().getIme())==0)
            {
                racunPosiljalac.setText("Broj Racuna:"+entry.getValue().getBrojRacuna());

            }
        }
    }
    /*OVA FUNKCIJA VRSI PROMENU ZIRORACUNA KOJI SE NALAZI ISPOD COMBOBOXA
    * U ZAVISNOSTI OD IZBORA UPISUJE SE ODGOVARAJUCI BROJ RACUNA CRVENIM SLOVIMA*/
    public void promena1(ActionEvent actionEvent) {
        racunPrimalac.setVisible(true);
        pr = (String) primalac.getValue();
        if(pr!=null)
            racunPrimalac.setText("");
        for (Map.Entry<Integer,Osoba> entry : mapa.entrySet())
        {

            if(entry.getKey()!=0 && pr.compareTo(entry.getValue().getIme())==0)
            {
                racunPrimalac.setText("Broj Racuna:"+entry.getValue().getBrojRacuna());

            }
        }
    }
}
