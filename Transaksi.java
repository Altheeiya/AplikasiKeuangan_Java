
import java.util.Date;

public class Transaksi extends AktivitasKeuangan {
    private Date tanggal;
    private String jenisTransaksi; // "Pemasukan" atau "Pengeluaran"
    private Kategori kategori;

    public Transaksi(String id, double jumlah, String deskripsi, AkunPengguna akun,
                     Date tanggal, String jenisTransaksi, Kategori kategori) {
        super(id, jumlah, deskripsi, akun);
        this.tanggal = tanggal;
        this.jenisTransaksi = jenisTransaksi;
        this.kategori = kategori;
    }

    public Date getTanggal() { return tanggal; }
    public String getJenisTransaksi() { return jenisTransaksi; }
    public Kategori getKategori() { return kategori; }
}
