import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KeuanganApp extends Application {

    private AkunPengguna akun = new AkunPengguna("1", "Kelompok2", "Kel2@gmail.com", 0);
    private ObservableList<Transaksi> dataTransaksi = FXCollections.observableArrayList();
    private Stage primaryStage;

    // UI elements for Transaksi (class fields for state persistence across scenes)
    private TextField idTransaksiField = new TextField();
    private TextField jumlahField = new TextField();
    private TextField deskripsiField = new TextField();
    private TextField kategoriTransaksiField = new TextField();
    private ComboBox<String> jenisCombo = new ComboBox<>();
    private TableView<Transaksi> transaksiTable; // Referensi tabel transaksi

    // UI elements for Anggaran (class fields for state persistence across scenes)
    private TextField idAnggaranField = new TextField();
    private TextField kategoriAnggaranField = new TextField();
    private TextField batasField = new TextField();

    // UI elements for Laporan Keuangan (class fields for state persistence across scenes)
    private TextArea laporanTextArea = new TextArea();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        jenisCombo.getItems().addAll("Pemasukan", "Pengeluaran");
        showOverallSplashScreen();
    }

    private void showOverallSplashScreen() {
        VBox splashLayout = new VBox(20);
        splashLayout.setAlignment(Pos.CENTER);
        splashLayout.setPadding(new Insets(50));
        splashLayout.setStyle("-fx-background-color: #e0f2f7;");

        Label titleLabel = new Label("APLIKASI KEUANGAN");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: #01579b;");

        Label subtitleLabel = new Label("Kelola Keuangan Anda dengan Mudah!");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitleLabel.setStyle("-fx-text-fill: #26c6da;");

        Button continueButton = new Button("Mulai Aplikasi");
        continueButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        continueButton.setStyle("-fx-background-color: #00838f; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        continueButton.setOnAction(e -> showFeatureSelectionScreen());

        splashLayout.getChildren().addAll(titleLabel, subtitleLabel, continueButton);

        Scene splashScene = new Scene(splashLayout, 600, 400);
        primaryStage.setTitle("Selamat Datang di Aplikasi Keuangan!");
        primaryStage.setScene(splashScene);
        primaryStage.show();
    }

    private void showFeatureSelectionScreen() {
        VBox selectionLayout = new VBox(25);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setPadding(new Insets(40));
        selectionLayout.setStyle("-fx-background-color: #fce4ec;");

        Label headerLabel = new Label("Pilih Modul yang Ingin Anda Akses:");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        headerLabel.setStyle("-fx-text-fill: #ad1457;");

        Button transaksiButton = createFeatureButton("Manajemen Transaksi");
        Button anggaranButton = createFeatureButton("Manajemen Anggaran");
        Button laporanButton = createFeatureButton("Lihat Laporan Keuangan (Ringkasan Total)");

        transaksiButton.setOnAction(e -> showTransaksiManagementScreen());
        anggaranButton.setOnAction(e -> showAnggaranManagementScreen());
        laporanButton.setOnAction(e -> showOverallReportScreen());

        selectionLayout.getChildren().addAll(headerLabel, transaksiButton, anggaranButton, laporanButton);

        Scene selectionScene = new Scene(selectionLayout, 650, 450);
        primaryStage.setTitle("Pilih Modul");
        primaryStage.setScene(selectionScene);
    }

    private Button createFeatureButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
        button.setPrefWidth(350); // Lebar tombol seragam
        button.setPrefHeight(60);
        button.setStyle("-fx-background-color: #ec407a; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-border-color: #d81b60; -fx-border-width: 2px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #d81b60; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-border-color: #ad1457; -fx-border-width: 2px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #ec407a; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-border-color: #d81b60; -fx-border-width: 2px;"));
        return button;
    }

    private void showTransaksiManagementScreen() {
        // --- FORM TAMBAH TRANSAKSI ---
        GridPane formTransaksi = new GridPane();
        formTransaksi.setPadding(new Insets(10));
        formTransaksi.setHgap(10);
        formTransaksi.setVgap(10);
        formTransaksi.add(new Label("ID Transaksi:"), 0, 0);
        formTransaksi.add(idTransaksiField, 1, 0);
        formTransaksi.add(new Label("Jumlah:"), 0, 1);
        formTransaksi.add(jumlahField, 1, 1);
        formTransaksi.add(new Label("Deskripsi:"), 0, 2);
        formTransaksi.add(deskripsiField, 1, 2);
        formTransaksi.add(new Label("Jenis:"), 0, 3);
        formTransaksi.add(jenisCombo, 1, 3);
        formTransaksi.add(new Label("Kategori:"), 0, 4);
        formTransaksi.add(kategoriTransaksiField, 1, 4);
        Button tambahTransaksiBtn = new Button("Tambah Transaksi");
        tambahTransaksiBtn.setMaxWidth(Double.MAX_VALUE);
        formTransaksi.add(tambahTransaksiBtn, 0, 5, 2, 1);

        // --- TABEL TRANSAKSI ---
        transaksiTable = new TableView<>(dataTransaksi);
        TableColumn<Transaksi, String> colID = new TableColumn<>("ID");
        TableColumn<Transaksi, String> colJenis = new TableColumn<>("Jenis");
        TableColumn<Transaksi, Double> colJumlah = new TableColumn<>("Jumlah");
        TableColumn<Transaksi, String> colKategori = new TableColumn<>("Kategori");
        TableColumn<Transaksi, String> colDeskripsi = new TableColumn<>("Deskripsi");
        TableColumn<Transaksi, String> colTanggal = new TableColumn<>("Tanggal");

        colID.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colJenis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJenisTransaksi()));
        colJumlah.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getJumlah()).asObject());
        colKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKategori().getNamaKategori()));
        colDeskripsi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeskripsi()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        colTanggal.setCellValueFactory(c -> {
            Date tanggal = c.getValue().getTanggal();
            return new SimpleStringProperty(tanggal != null ? dateFormatter.format(tanggal) : "-");
        });

        transaksiTable.getColumns().addAll(colID, colJenis, colJumlah, colKategori, colDeskripsi, colTanggal);
        transaksiTable.setPrefHeight(300);

        // --- AKSI TAMBAH TRANSAKSI ---
        tambahTransaksiBtn.setOnAction(e -> {
            try {
                String id = idTransaksiField.getText();
                double jumlah = Double.parseDouble(jumlahField.getText());
                String deskripsi = deskripsiField.getText();
                String jenis = jenisCombo.getValue();
                String namaKategori = kategoriTransaksiField.getText();

                if (id.isEmpty() || deskripsi.isEmpty() || namaKategori.isEmpty() || jenis == null) {
                    showAlert("Harap isi semua field Transaksi.");
                    return;
                }

                // Validasi batas anggaran
                if (jenis.equalsIgnoreCase("Pengeluaran")) {
                    boolean anggaranDitemukan = false;
                    for (Anggaran a : akun.getAnggaranList()) {
                        if (a.getKategori().getNamaKategori().equalsIgnoreCase(namaKategori)) {
                            anggaranDitemukan = true;
                            double totalPengeluaranKategoriSaatIni = akun.getTransaksiList().stream()
                                    .filter(t -> t.getJenisTransaksi().equalsIgnoreCase("Pengeluaran"))
                                    .filter(t -> t.getKategori().getNamaKategori().equalsIgnoreCase(namaKategori))
                                    .mapToDouble(Transaksi::getJumlah)
                                    .sum();

                            if ((totalPengeluaranKategoriSaatIni + jumlah) > a.getBatas()) {
                                showAlert("Pengeluaran melebihi batas anggaran kategori: " + namaKategori + ".\nBatas: Rp " + a.getBatas() + "\nTerpakai: Rp " + totalPengeluaranKategoriSaatIni + "\nPengeluaran Baru: Rp " + jumlah);
                                return;
                            }
                        }
                    }
                    if (!anggaranDitemukan) {
                        showAlert("Tidak ada anggaran yang ditetapkan untuk kategori '" + namaKategori + "'. Pengeluaran akan ditambahkan tanpa validasi anggaran.");
                    }
                }

                Kategori kategori = new Kategori("KAT" + namaKategori.hashCode(), namaKategori);
                Transaksi transaksi = new Transaksi(id, jumlah, deskripsi, akun, new Date(), jenis, kategori);
                akun.tambahTransaksi(transaksi);
                dataTransaksi.add(transaksi);
                clearTransaksiFields();
                showAlert("Transaksi berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showAlert("Jumlah harus berupa angka.");
            }
        });

        Button backButton = new Button("Kembali ke Menu Utama");
        backButton.setOnAction(e -> showFeatureSelectionScreen());
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");


        VBox transaksiLayout = new VBox(15, new Label("--- Manajemen Transaksi ---"), formTransaksi, new Separator(), new Label("--- Daftar Transaksi ---"), transaksiTable, backButton);
        transaksiLayout.setPadding(new Insets(20));
        transaksiLayout.setAlignment(Pos.TOP_CENTER);

        Scene transaksiScene = new Scene(transaksiLayout, 800, 700);
        primaryStage.setTitle("Manajemen Transaksi");
        primaryStage.setScene(transaksiScene);
    }

    private void showAnggaranManagementScreen() {
        // --- FORM TAMBAH ANGGARAN ---
        GridPane formAnggaran = new GridPane();
        formAnggaran.setPadding(new Insets(10));
        formAnggaran.setHgap(10);
        formAnggaran.setVgap(10);
        formAnggaran.add(new Label("ID Anggaran:"), 0, 0);
        formAnggaran.add(idAnggaranField, 1, 0);
        formAnggaran.add(new Label("Kategori:"), 0, 1);
        formAnggaran.add(kategoriAnggaranField, 1, 1);
        formAnggaran.add(new Label("Batas (Rp):"), 0, 2);
        formAnggaran.add(batasField, 1, 2);
        Button tambahAnggaranBtn = new Button("Tambah Anggaran");
        tambahAnggaranBtn.setMaxWidth(Double.MAX_VALUE);
        formAnggaran.add(tambahAnggaranBtn, 0, 3, 2, 1);

        // --- AKSI TAMBAH ANGGARAN ---
        tambahAnggaranBtn.setOnAction(e -> {
            try {
                String id = idAnggaranField.getText();
                String kategoriNama = kategoriAnggaranField.getText();
                double batas = Double.parseDouble(batasField.getText());

                if (id.isEmpty() || kategoriNama.isEmpty()) {
                    showAlert("Harap isi semua field Anggaran.");
                    return;
                }
                
                boolean categoryExists = false;
                for (Anggaran a : akun.getAnggaranList()) {
                    if (a.getKategori().getNamaKategori().equalsIgnoreCase(kategoriNama)) {
                        showAlert("Anggaran untuk kategori '" + kategoriNama + "' sudah ada.");
                        categoryExists = true;
                        break;
                    }
                }

                if (!categoryExists) {
                    Kategori kat = new Kategori("KAT" + kategoriNama.hashCode(), kategoriNama);
                    Anggaran anggaran = new Anggaran(id, kat, batas, akun);
                    akun.tambahAnggaran(anggaran);
                    showAlert("Anggaran berhasil ditambahkan.");
                    clearAnggaranFields();
                }

            } catch (NumberFormatException ex) {
                showAlert("Batas harus berupa angka.");
            }
        });

        Button backButton = new Button("Kembali ke Menu Utama");
        backButton.setOnAction(e -> showFeatureSelectionScreen());
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");

        VBox anggaranLayout = new VBox(15, new Label("--- Manajemen Anggaran ---"), formAnggaran, new Separator(), backButton);
        anggaranLayout.setPadding(new Insets(20));
        anggaranLayout.setAlignment(Pos.TOP_CENTER);

        Scene anggaranScene = new Scene(anggaranLayout, 600, 400);
        primaryStage.setTitle("Manajemen Anggaran");
        primaryStage.setScene(anggaranScene);
    }

    private void showOverallReportScreen() {
        String reportText = generateReportString(); // Dapatkan teks laporan
        laporanTextArea.setText(reportText); // Set teks ke laporanTextArea di layar ini

        Label reportTitleLabel = new Label("--- Laporan Keuangan Keseluruhan ---");
        reportTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        reportTitleLabel.setAlignment(Pos.CENTER);
        reportTitleLabel.setMaxWidth(Double.MAX_VALUE);

        Button printPreviewButton = new Button("Tampilkan Pratinjau Laporan"); // Ubah nama tombol
        printPreviewButton.setMaxWidth(Double.MAX_VALUE);
        printPreviewButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        printPreviewButton.setOnAction(e -> showPrintPreview(reportText)); // Panggil metode pratinjau dengan teks laporan

        Button backButton = new Button("Kembali ke Menu Utama");
        backButton.setOnAction(e -> showFeatureSelectionScreen());
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");

        VBox laporanLayout = new VBox(15, reportTitleLabel, laporanTextArea, printPreviewButton, backButton);
        laporanLayout.setPadding(new Insets(20));
        laporanLayout.setAlignment(Pos.TOP_CENTER);

        Scene laporanScene = new Scene(laporanLayout, 700, 600);
        primaryStage.setTitle("Laporan Keuangan");
        primaryStage.setScene(laporanScene);
    }

    /**
     * Metode baru untuk menampilkan pratinjau laporan di jendela pop-up.
     * @param reportText Teks laporan yang akan ditampilkan.
     */
    private void showPrintPreview(String reportText) {
        Stage printPreviewStage = new Stage();
        printPreviewStage.initOwner(primaryStage); // Membuat jendela pop-up ini 'anak' dari jendela utama
        printPreviewStage.initModality(Modality.APPLICATION_MODAL); // Opsional: Membuat jendela ini memblokir interaksi dengan jendela utama sampai ditutup

        TextArea previewTextArea = new TextArea();
        previewTextArea.setEditable(false); // Tidak bisa diedit
        previewTextArea.setText(reportText); // Set teks laporan ke TextArea pratinjau
        previewTextArea.setFont(Font.font("Monospaced", 12)); // Opsional: Atur font monospace untuk keterbacaan yang lebih baik
        previewTextArea.setWrapText(true); // Opsional: Teks akan membungkus jika terlalu panjang

        VBox layout = new VBox(10, new Label("Pratinjau Laporan Keuangan"), previewTextArea);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene previewScene = new Scene(layout, 600, 700); // Ukuran jendela pratinjau
        printPreviewStage.setScene(previewScene);
        printPreviewStage.setTitle("Pratinjau Cetak Laporan");
        printPreviewStage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void clearTransaksiFields() {
        idTransaksiField.clear();
        jumlahField.clear();
        deskripsiField.clear();
        kategoriTransaksiField.clear();
        jenisCombo.getSelectionModel().clearSelection();
    }

    private void clearAnggaranFields() {
        idAnggaranField.clear();
        kategoriAnggaranField.clear();
        batasField.clear();
    }

    /**
     * Menggenerasi teks laporan keuangan dan mengembalikannya sebagai String.
     * @return String berisi laporan keuangan yang diformat.
     */
    private String generateReportString() {
        double totalPemasukan = 0;
        double totalPengeluaran = 0;

        for (Transaksi t : akun.getTransaksiList()) {
            if (t.getJenisTransaksi().equalsIgnoreCase("Pemasukan")) {
                totalPemasukan += t.getJumlah();
            } else if (t.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
                totalPengeluaran += t.getJumlah();
            }
        }

        double saldoAkhir = akun.getSaldo();

        StringBuilder laporan = new StringBuilder();
        laporan.append("--- Ringkasan Akun ---\n");
        laporan.append(String.format("ID Akun        : %s\n", akun.getIdAkun()));
        laporan.append(String.format("Nama Akun      : %s\n", akun.getNama()));
        laporan.append(String.format("Email Akun     : %s\n", akun.getEmail()));
        laporan.append(String.format("Saldo Saat Ini : Rp %.2f\n", saldoAkhir));
        laporan.append(String.format("Total Pemasukan: Rp %.2f\n", totalPemasukan));
        laporan.append(String.format("Total Pengeluaran: Rp %.2f\n", totalPengeluaran));
        
        laporan.append("\n--- Daftar Anggaran ---\n");
        if (akun.getAnggaranList().isEmpty()) {
            laporan.append("Tidak ada anggaran yang ditetapkan.\n");
        } else {
            for (Anggaran a : akun.getAnggaranList()) {
                double pengeluaranKategori = akun.getTransaksiList().stream()
                        .filter(t -> t.getJenisTransaksi().equalsIgnoreCase("Pengeluaran"))
                        .filter(t -> t.getKategori().getIdKategori().equals(a.getKategori().getIdKategori()))
                        .mapToDouble(Transaksi::getJumlah)
                        .sum();
                laporan.append(String.format("Kategori: %s, Batas: Rp %.2f, Terpakai: Rp %.2f\n",
                        a.getKategori().getNamaKategori(), a.getBatas(), pengeluaranKategori));
            }
        }
        laporan.append("\n--- Jumlah Transaksi: %d ---\n".formatted(akun.getTransaksiList().size()));


        return laporan.toString(); // Mengembalikan string laporan
    }
}