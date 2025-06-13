
public class Anggaran {
    private String idAnggaran;
    private Kategori kategori;
    private double batas;
    private AkunPengguna akun;

    public Anggaran(String idAnggaran, Kategori kategori, double batas, AkunPengguna akun) {
        this.idAnggaran = idAnggaran;
        this.kategori = kategori;
        this.batas = batas;
        this.akun = akun;
    }

    public String getIdAnggaran() { return idAnggaran; }
    public Kategori getKategori() { return kategori; }
    public double getBatas() { return batas; }
    public AkunPengguna getAkun() { return akun; }
}
