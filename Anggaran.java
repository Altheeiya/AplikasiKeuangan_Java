public class Anggaran {
    private String idAnggaran;
    private Kategori kategori;
    private double batas;
    private AkunPengguna akun;
    private String tipeAnggaran; // PERUBAHAN: "Pemasukan" atau "Pengeluaran"

    public Anggaran(String idAnggaran, Kategori kategori, double batas, AkunPengguna akun, String tipeAnggaran) {
        this.idAnggaran = idAnggaran;
        this.kategori = kategori;
        this.batas = batas;
        this.akun = akun;
        this.tipeAnggaran = tipeAnggaran; // PERUBAHAN
    }

    public String getIdAnggaran() { return idAnggaran; }
    public Kategori getKategori() { return kategori; }
    public double getBatas() { return batas; }
    public AkunPengguna getAkun() { return akun; }
    public String getTipeAnggaran() { return tipeAnggaran; } // PERUBAHAN
}