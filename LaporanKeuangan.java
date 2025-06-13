
public class LaporanKeuangan {
    private String idLaporan;
    private String periode;
    private double totalPemasukan;
    private double totalPengeluaran;
    private double saldoAkhir;
    private AkunPengguna akun;

    public LaporanKeuangan(String idLaporan, String periode, double totalPemasukan, double totalPengeluaran, double saldoAkhir, AkunPengguna akun) {
        this.idLaporan = idLaporan;
        this.periode = periode;
        this.totalPemasukan = totalPemasukan;
        this.totalPengeluaran = totalPengeluaran;
        this.saldoAkhir = saldoAkhir;
        this.akun = akun;
    }

    public void tampilkanLaporan() {
        System.out.println("--- Laporan Keuangan ---");
        System.out.println("ID Laporan     : " + idLaporan);
        System.out.println("Periode        : " + periode);
        System.out.println("Pemasukan Total: " + totalPemasukan);
        System.out.println("Pengeluaran Total: " + totalPengeluaran);
        System.out.println("Saldo Akhir    : " + saldoAkhir);
        System.out.println("Akun ID        : " + akun.getIdAkun());
    }
}
