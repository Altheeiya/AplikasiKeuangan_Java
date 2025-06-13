
public class AktivitasKeuangan {
    private String id;
    private double jumlah;
    private String deskripsi;
    private AkunPengguna akun;

    public AktivitasKeuangan(String id, double jumlah, String deskripsi, AkunPengguna akun) {
        this.id = id;
        this.jumlah = jumlah;
        this.deskripsi = deskripsi;
        this.akun = akun;
    }

    public String getId() { return id; }
    public double getJumlah() { return jumlah; }
    public String getDeskripsi() { return deskripsi; }
    public AkunPengguna getAkun() { return akun; }
}
