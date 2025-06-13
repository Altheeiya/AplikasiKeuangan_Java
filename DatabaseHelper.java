import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Kelas ini bertanggung jawab untuk semua komunikasi dengan database SQLite.
 * Mengelola koneksi, pembuatan tabel, dan operasi CRUD (Create, Read, Update, Delete).
 */
public class DatabaseHelper {
    // URL koneksi JDBC untuk SQLite. Ini akan membuat file 'keuangan.db' di folder proyek.
    private static final String URL = "jdbc:sqlite:keuangan.db";

    /**
     * Membuat koneksi ke database.
     * @return Objek Connection yang aktif atau null jika gagal.
     */
    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error koneksi ke SQLite: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Inisialisasi database. Membuat semua tabel yang diperlukan jika belum ada.
     * Wajib dipanggil sekali saat aplikasi pertama kali dijalankan.
     */
    public static void initializeDatabase() {
        String createKategoriTable = "CREATE TABLE IF NOT EXISTS kategori ("
                                    + " id_kategori TEXT PRIMARY KEY,"
                                    + " nama_kategori TEXT NOT NULL UNIQUE"
                                    + ");";

        String createAnggaranTable = "CREATE TABLE IF NOT EXISTS anggaran ("
                                    + " id_anggaran TEXT PRIMARY KEY,"
                                    + " id_kategori TEXT NOT NULL,"
                                    + " batas REAL NOT NULL,"
                                    + " tipe_anggaran TEXT NOT NULL,"
                                    + " FOREIGN KEY (id_kategori) REFERENCES kategori (id_kategori)"
                                    + ");";

        String createTransaksiTable = "CREATE TABLE IF NOT EXISTS transaksi ("
                                    + " id_transaksi TEXT PRIMARY KEY,"
                                    + " jumlah REAL NOT NULL,"
                                    + " deskripsi TEXT,"
                                    + " tanggal TEXT NOT NULL,"
                                    + " jenis_transaksi TEXT NOT NULL,"
                                    + " id_kategori TEXT NOT NULL,"
                                    + " FOREIGN KEY (id_kategori) REFERENCES kategori (id_kategori)"
                                    + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createKategoriTable);
            stmt.execute(createAnggaranTable);
            stmt.execute(createTransaksiTable);
        } catch (SQLException e) {
            System.out.println("Error saat membuat tabel: " + e.getMessage());
        }
    }

    //== CRUD UNTUK KATEGORI & ANGGARAN ==//

    public static void insertKategori(Kategori kategori) {
        String sql = "INSERT INTO kategori(id_kategori, nama_kategori) VALUES(?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategori.getIdKategori());
            pstmt.setString(2, kategori.getNamaKategori());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (!e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                System.out.println("Error insert kategori: " + e.getMessage());
            }
        }
    }
    
    public static void insertAnggaran(Anggaran anggaran) {
        insertKategori(anggaran.getKategori());
        String sql = "INSERT INTO anggaran(id_anggaran, id_kategori, batas, tipe_anggaran) VALUES(?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anggaran.getIdAnggaran());
            pstmt.setString(2, anggaran.getKategori().getIdKategori());
            pstmt.setDouble(3, anggaran.getBatas());
            pstmt.setString(4, anggaran.getTipeAnggaran());
            pstmt.executeUpdate();
        } catch (SQLException e) {
             System.out.println("Error insert anggaran: " + e.getMessage());
        }
    }

    public static Kategori getKategoriById(String idKategori) {
        String sql = "SELECT nama_kategori FROM kategori WHERE id_kategori = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idKategori);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Kategori(idKategori, rs.getString("nama_kategori"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public static List<Anggaran> loadAnggaran(AkunPengguna akun) {
        String sql = "SELECT * FROM anggaran";
        List<Anggaran> anggaranList = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Kategori kategori = getKategoriById(rs.getString("id_kategori"));
                if (kategori != null) {
                    Anggaran ang = new Anggaran(
                        rs.getString("id_anggaran"),
                        kategori,
                        rs.getDouble("batas"),
                        akun,
                        rs.getString("tipe_anggaran")
                    );
                    anggaranList.add(ang);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error load anggaran: " + e.getMessage());
        }
        return anggaranList;
    }

    //== CRUD UNTUK TRANSAKSI ==//

    public static void insertTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO transaksi(id_transaksi, jumlah, deskripsi, tanggal, jenis_transaksi, id_kategori) VALUES(?,?,?,?,?,?)";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaksi.getId());
            pstmt.setDouble(2, transaksi.getJumlah());
            pstmt.setString(3, transaksi.getDeskripsi());
            pstmt.setString(4, sdf.format(transaksi.getTanggal()));
            pstmt.setString(5, transaksi.getJenisTransaksi());
            pstmt.setString(6, transaksi.getKategori().getIdKategori());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insert transaksi: " + e.getMessage());
        }
    }

    public static List<Transaksi> loadTransaksi(AkunPengguna akun) {
        String sql = "SELECT * FROM transaksi ORDER BY tanggal ASC";
        List<Transaksi> transaksiList = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Kategori kategori = getKategoriById(rs.getString("id_kategori"));
                if (kategori != null) {
                    Transaksi trx = new Transaksi(
                        rs.getString("id_transaksi"),
                        rs.getDouble("jumlah"),
                        rs.getString("deskripsi"),
                        akun,
                        sdf.parse(rs.getString("tanggal")),
                        rs.getString("jenis_transaksi"),
                        kategori
                    );
                    transaksiList.add(trx);
                }
            }
        } catch (Exception e) {
            System.out.println("Error load transaksi: " + e.getMessage());
        }
        return transaksiList;
    }

    public static void deleteTransaksi(String id) {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error delete transaksi: " + e.getMessage());
        }
    }

    public static void updateTransaksi(Transaksi transaksi) {
        String sql = "UPDATE transaksi SET jumlah = ?, deskripsi = ? WHERE id_transaksi = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, transaksi.getJumlah());
            pstmt.setString(2, transaksi.getDeskripsi());
            pstmt.setString(3, transaksi.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error update transaksi: " + e.getMessage());
        }
    }
}