import java.util.ArrayList;
import java.util.List;

public class AkunPengguna {
    private String idAkun;
    private String nama;
    private String email;
    private double saldo;
    private List<Transaksi> transaksiList;
    private List<Anggaran> anggaranList;

    public AkunPengguna(String idAkun, String nama, String email, double saldo) {
        this.idAkun = idAkun;
        this.nama = nama;
        this.email = email;
        this.saldo = saldo;
        this.transaksiList = new ArrayList<>();
        this.anggaranList = new ArrayList<>();
    }

    public String getIdAkun() { return idAkun; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public double getSaldo() { return saldo; }
    public List<Transaksi> getTransaksiList() { return transaksiList; }
    public List<Anggaran> getAnggaranList() { return anggaranList; }

    public void tambahTransaksi(Transaksi transaksi) {
        if (transaksi.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
            saldo -= transaksi.getJumlah();
        } else {
            saldo += transaksi.getJumlah();
        }
        transaksiList.add(transaksi);
    }
    
    public void hapusTransaksi(Transaksi transaksi) {
        if (transaksi.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
            saldo += transaksi.getJumlah();
        } else {
            saldo -= transaksi.getJumlah();
        }
        transaksiList.remove(transaksi);
    }
    
    public void editTransaksi(Transaksi transaksiLama, Transaksi transaksiBaru) {
        if (transaksiLama.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
            saldo += transaksiLama.getJumlah();
        } else {
            saldo -= transaksiLama.getJumlah();
        }
        
        if (transaksiBaru.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
            saldo -= transaksiBaru.getJumlah();
        } else {
            saldo += transaksiBaru.getJumlah();
        }
        
        int index = transaksiList.indexOf(transaksiLama);
        if (index != -1) {
            transaksiList.set(index, transaksiBaru);
        }
    }

    public void tambahAnggaran(Anggaran anggaran) {
        anggaranList.add(anggaran);
    }
    
    public void recalculateSaldo() {
        this.saldo = 0;
        for (Transaksi transaksi : this.transaksiList) {
            if (transaksi.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
                this.saldo -= transaksi.getJumlah();
            } else {
                this.saldo += transaksi.getJumlah();
            }
        }
    }
}