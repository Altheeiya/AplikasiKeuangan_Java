
public class Pembayaran {
    private String idPembayaran;
    private double jumlah;
    private String metodePembayaran;
    private Transaksi transaksi;

    public Pembayaran(String idPembayaran, double jumlah, String metodePembayaran, Transaksi transaksi) {
        this.idPembayaran = idPembayaran;
        this.jumlah = jumlah;
        this.metodePembayaran = metodePembayaran;
        this.transaksi = transaksi;
    }

    public String getIdPembayaran() { return idPembayaran; }
    public double getJumlah() { return jumlah; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public Transaksi getTransaksi() { return transaksi; }
}
