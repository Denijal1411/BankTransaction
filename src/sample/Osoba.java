package sample;

    public class Osoba {
        String ime  ,prezime  ,brojRacuna  ,banka ;
        String stanje;

        public Osoba(String ime, String prezime, String brojRacuna, String stanje, String banka) {
            this.ime = ime;
            this.prezime = prezime;
            this.brojRacuna = brojRacuna;
            this.stanje = stanje;
            this.banka = banka;
        }

        public String getIme() {
            return ime;
        }

        public void setIme(String ime) {
            this.ime = ime;
        }

        public String getPrezime() {
            return prezime;
        }

        public void setPrezime(String prezime) {
            this.prezime = prezime;
        }

        public String getBrojRacuna() {
            return brojRacuna;
        }

        public void setBrojRacuna(String brojRacuna) {
            this.brojRacuna = brojRacuna;
        }

        public String   getStanje() {
            return stanje;
        }

        public void setStanje(String stanje) {
            this.stanje = stanje;
        }

        public String getBanka() {
            return banka;
        }

        public void setBanka(String banka) {
            this.banka = banka;
        }
        public String toString(){
            return "|"+getIme()+"| "+getBanka()+"| ["+getBrojRacuna()+"]="+getStanje();
        }
    }

