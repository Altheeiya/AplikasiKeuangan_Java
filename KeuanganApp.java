import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

public class KeuanganApp extends Application {

    private AkunPengguna akun = new AkunPengguna("1", "Kelompok2", "Kel2@gmail.com", 0);
    private ObservableList<Transaksi> dataTransaksi = FXCollections.observableArrayList();
    private Stage primaryStage;
    private int idTransaksiCounter = 1;
    private int idAnggaranCounter = 1;
    private TextField jumlahField = new TextField();
    private TextField deskripsiField = new TextField();
    private ComboBox<String> jenisCombo = new ComboBox<>();
    private ComboBox<String> kategoriCombo = new ComboBox<>();
    private ObservableList<String> namaKategoriList = FXCollections.observableArrayList();
    private TableView<Transaksi> transaksiTable;
    private TextArea laporanTextArea = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        DatabaseHelper.initializeDatabase();
        this.primaryStage = stage;
        akun.getAnggaranList().clear();
        akun.getAnggaranList().addAll(DatabaseHelper.loadAnggaran(this.akun));
        dataTransaksi.clear();
        dataTransaksi.addAll(DatabaseHelper.loadTransaksi(this.akun));
        akun.getTransaksiList().clear();
        akun.getTransaksiList().addAll(dataTransaksi);
        akun.recalculateSaldo();
        updateCounters();
        jenisCombo.getItems().addAll("Pemasukan", "Pengeluaran");
        kategoriCombo.setItems(namaKategoriList);
        jenisCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            filterKategoriBerdasarkanJenis(newValue);
        });
        showOverallSplashScreen();
    }

    private void updateCounters() {
        if (!dataTransaksi.isEmpty()) {
            int maxTrxId = dataTransaksi.stream()
                .map(t -> t.getId().replace("TRX-", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
            idTransaksiCounter = maxTrxId + 1;
        }
        if (!akun.getAnggaranList().isEmpty()) {
            int maxAnggaranId = akun.getAnggaranList().stream()
                .map(a -> a.getIdAnggaran().replace("ANG-", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
            idAnggaranCounter = maxAnggaranId + 1;
        }
    }
    
    private void filterKategoriBerdasarkanJenis(String jenisTransaksi) {
        kategoriCombo.getSelectionModel().clearSelection();
        if (jenisTransaksi == null) {
            namaKategoriList.clear();
        } else {
            namaKategoriList.setAll(
                akun.getAnggaranList().stream()
                    .filter(anggaran -> anggaran.getTipeAnggaran().equalsIgnoreCase(jenisTransaksi))
                    .map(anggaran -> anggaran.getKategori().getNamaKategori())
                    .collect(Collectors.toList())
            );
        }
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
        continueButton.setOnAction(e -> menampilkanFitur());
        splashLayout.getChildren().addAll(titleLabel, subtitleLabel, continueButton);
        Scene splashScene = new Scene(splashLayout, 600, 400);
        primaryStage.setTitle("Selamat Datang di Aplikasi Keuangan!");
        primaryStage.setScene(splashScene);
        primaryStage.show();
    }

    private void menampilkanFitur() {
        VBox selectionLayout = new VBox(25);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setPadding(new Insets(40));
        selectionLayout.setStyle("-fx-background-color: #fce4ec;");
        Label headerLabel = new Label("Pilih Modul yang Ingin Anda Akses:");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        headerLabel.setStyle("-fx-text-fill: #ad1457;");
        Button transaksiButton = createFeatureButton("Manajemen Transaksi");
        Button laporanButton = createFeatureButton("Lihat Laporan Keuangan");
        transaksiButton.setOnAction(e -> showTransaksiManagementScreen());
        laporanButton.setOnAction(e -> showOverallReportScreen());
        selectionLayout.getChildren().addAll(headerLabel, transaksiButton, laporanButton);
        Scene selectionScene = new Scene(selectionLayout, 650, 450);
        primaryStage.setTitle("Pilih Modul");
        primaryStage.setScene(selectionScene);
    }

    private Button createFeatureButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
        button.setPrefWidth(350);
        button.setPrefHeight(60);
        button.setStyle("-fx-background-color: #ec407a; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-border-color: #d81b60; -fx-border-width: 2px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #d81b60; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-border-color: #ad1457; -fx-border-width: 2px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #ec407a; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-border-color: #d81b60; -fx-border-width: 2px;"));
        return button;
    }

    private void showTransaksiManagementScreen() {
        GridPane formTransaksi = new GridPane();
        Button editBtn = new Button("Edit Transaksi");
        Button hapusBtn = new Button("Hapus Transaksi");
        HBox tabelActionButtons = new HBox(10, editBtn, hapusBtn);
        tabelActionButtons.setAlignment(Pos.CENTER_RIGHT);

        hapusBtn.setOnAction(e -> {
            Transaksi selected = transaksiTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Pilih transaksi yang ingin dihapus.");
                return;
            }
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText("Hapus Transaksi: " + selected.getDeskripsi());
            confirmAlert.setContentText("Anda yakin ingin menghapus transaksi ini?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                akun.hapusTransaksi(selected);
                DatabaseHelper.deleteTransaksi(selected.getId());
                dataTransaksi.remove(selected);
                transaksiTable.refresh();
                showAlert("Transaksi berhasil dihapus.");
            }
        });
        
        editBtn.setOnAction(e -> {
            Transaksi selected = transaksiTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Pilih transaksi yang ingin diedit.");
                return;
            }
            showEditTransaksiDialog(selected);
        });

        transaksiTable = new TableView<>(dataTransaksi);
        Button backButton = new Button("Kembali ke Menu Utama");
        VBox transaksiLayout = new VBox(15, new Label("--- Manajemen Transaksi ---"), formTransaksi, new Separator(), new Label("--- Daftar Transaksi ---"), transaksiTable, tabelActionButtons, backButton);
        formTransaksi.setPadding(new Insets(10));
        formTransaksi.setHgap(10);
        formTransaksi.setVgap(10);
        formTransaksi.add(new Label("Jenis:"), 0, 3);
        formTransaksi.add(jenisCombo, 1, 3);
        formTransaksi.add(new Label("Kategori:"), 0, 4);
        Button tambahKategoriBtn = new Button("+");
        tambahKategoriBtn.setTooltip(new Tooltip("Tambah kategori baru"));
        tambahKategoriBtn.setOnAction(e -> showTambahKategoriDialog());
        HBox kategoriLayout = new HBox(5, kategoriCombo, tambahKategoriBtn);
        kategoriCombo.setPrefWidth(150);
        formTransaksi.add(kategoriLayout, 1, 4);
        formTransaksi.add(new Label("Jumlah:"), 0, 1);
        formTransaksi.add(jumlahField, 1, 1);
        formTransaksi.add(new Label("Deskripsi:"), 0, 2);
        formTransaksi.add(deskripsiField, 1, 2);
        Button tambahTransaksiBtn = new Button("Tambah Transaksi");
        tambahTransaksiBtn.setMaxWidth(Double.MAX_VALUE);
        formTransaksi.add(tambahTransaksiBtn, 0, 5, 2, 1);
        
        tambahTransaksiBtn.setOnAction(e -> {
             try {
                String id = "TRX-" + idTransaksiCounter;
                double jumlah = Double.parseDouble(jumlahField.getText());
                String deskripsi = deskripsiField.getText();
                String jenis = jenisCombo.getValue();
                String namaKategori = kategoriCombo.getValue();

                if (deskripsi.isEmpty() || namaKategori == null || jenis == null) {
                    showAlert("Harap isi semua field.");
                    return;
                }
                
                Kategori kategori = new Kategori("KAT" + namaKategori.hashCode(), namaKategori);
                Transaksi transaksi = new Transaksi(id, jumlah, deskripsi, akun, new Date(), jenis, kategori);
                akun.tambahTransaksi(transaksi);
                DatabaseHelper.insertTransaksi(transaksi);
                dataTransaksi.add(transaksi);
                idTransaksiCounter++;
                clearTransaksiFields();
                showAlert("Transaksi berhasil ditambahkan.");
            } catch (NumberFormatException ex) {
                showAlert("Jumlah harus berupa angka.");
            }
        });
        
        TableColumn<Transaksi, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        TableColumn<Transaksi, String> colJenis = new TableColumn<>("Jenis");
        colJenis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJenisTransaksi()));
        TableColumn<Transaksi, Double> colJumlah = new TableColumn<>("Jumlah");
        colJumlah.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getJumlah()).asObject());
        TableColumn<Transaksi, String> colKategori = new TableColumn<>("Kategori");
        colKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKategori().getNamaKategori()));
        TableColumn<Transaksi, String> colDeskripsi = new TableColumn<>("Deskripsi");
        colDeskripsi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeskripsi()));
        TableColumn<Transaksi, String> colTanggal = new TableColumn<>("Tanggal");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        colTanggal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTanggal() != null ? dateFormatter.format(c.getValue().getTanggal()) : "-"));
        transaksiTable.getColumns().setAll(colID, colJenis, colJumlah, colKategori, colDeskripsi, colTanggal);
        transaksiTable.setPrefHeight(300);

        backButton.setOnAction(e -> menampilkanFitur());
        backButton.setMaxWidth(Double.MAX_VALUE);
        transaksiLayout.setPadding(new Insets(20));
        transaksiLayout.setAlignment(Pos.TOP_CENTER);
        Scene transaksiScene = new Scene(transaksiLayout, 800, 750);
        primaryStage.setTitle("Manajemen Transaksi");
        primaryStage.setScene(transaksiScene);
    }
    
    private void showEditTransaksiDialog(Transaksi transaksiLama) {
        Dialog<Transaksi> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaksi");
        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField editJumlahField = new TextField(String.valueOf(transaksiLama.getJumlah()));
        TextField editDeskripsiField = new TextField(transaksiLama.getDeskripsi());
        grid.add(new Label("Deskripsi:"), 0, 0);
        grid.add(editDeskripsiField, 1, 0);
        grid.add(new Label("Jumlah:"), 0, 1);
        grid.add(editJumlahField, 1, 1);
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                try {
                    double jumlahBaru = Double.parseDouble(editJumlahField.getText());
                    String deskripsiBaru = editDeskripsiField.getText();
                    if (deskripsiBaru.isEmpty()) {
                        showAlert("Deskripsi tidak boleh kosong.");
                        return null;
                    }
                    return new Transaksi(
                        transaksiLama.getId(),
                        jumlahBaru,
                        deskripsiBaru,
                        transaksiLama.getAkun(),
                        transaksiLama.getTanggal(),
                        transaksiLama.getJenisTransaksi(),
                        transaksiLama.getKategori()
                    );
                } catch (NumberFormatException e) {
                    showAlert("Jumlah harus berupa angka.");
                    return null;
                }
            }
            return null;
        });

        Optional<Transaksi> result = dialog.showAndWait();
        result.ifPresent(transaksiBaru -> {
            akun.editTransaksi(transaksiLama, transaksiBaru);
            DatabaseHelper.updateTransaksi(transaksiBaru);
            int index = dataTransaksi.indexOf(transaksiLama);
            if (index != -1) {
                dataTransaksi.set(index, transaksiBaru);
            }
            transaksiTable.refresh();
            showAlert("Transaksi berhasil diperbarui.");
        });
    }
    
    private void showTambahKategoriDialog() {
        Dialog<Anggaran> dialog = new Dialog<>();
        dialog.setTitle("Tambah Kategori Baru");
        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField namaKategoriField = new TextField();
        namaKategoriField.setPromptText("Contoh: Gaji, Makanan");
        ComboBox<String> tipeKategoriCombo = new ComboBox<>();
        tipeKategoriCombo.getItems().addAll("Pemasukan", "Pengeluaran");
        TextField batasAnggaranField = new TextField("0");
        batasAnggaranField.setPromptText("Contoh: 500000");
        Label batasLabel = new Label("Batas Anggaran (Rp):");
        batasAnggaranField.setVisible(false);
        batasLabel.setVisible(false);
        tipeKategoriCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            boolean isPengeluaran = "Pengeluaran".equals(newValue);
            batasAnggaranField.setVisible(isPengeluaran);
            batasLabel.setVisible(isPengeluaran);
        });
        grid.add(new Label("Nama Kategori:"), 0, 0);
        grid.add(namaKategoriField, 1, 0);
        grid.add(new Label("Tipe Kategori:"), 0, 1);
        grid.add(tipeKategoriCombo, 1, 1);
        grid.add(batasLabel, 0, 2);
        grid.add(batasAnggaranField, 1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                String nama = namaKategoriField.getText();
                String tipe = tipeKategoriCombo.getValue();
                double batas = 0;
                if (nama.isEmpty() || tipe == null) {
                    showAlert("Nama dan Tipe Kategori tidak boleh kosong.");
                    return null;
                }
                if (tipe.equals("Pengeluaran")) {
                    try {
                        batas = Double.parseDouble(batasAnggaranField.getText());
                    } catch (NumberFormatException e) {
                        showAlert("Batas anggaran harus berupa angka.");
                        return null;
                    }
                }
                for(Anggaran ang : akun.getAnggaranList()) {
                    if(ang.getKategori().getNamaKategori().equalsIgnoreCase(nama)) {
                        showAlert("Kategori '" + nama + "' sudah ada.");
                        return null;
                    }
                }
                String id = "ANG-" + idAnggaranCounter;
                Kategori kat = new Kategori("KAT" + nama.hashCode(), nama);
                return new Anggaran(id, kat, batas, akun, tipe);
            }
            return null;
        });

        Optional<Anggaran> result = dialog.showAndWait();
        result.ifPresent(anggaran -> {
            akun.tambahAnggaran(anggaran);
            DatabaseHelper.insertAnggaran(anggaran);
            idAnggaranCounter++;
            jenisCombo.getSelectionModel().select(anggaran.getTipeAnggaran());
            filterKategoriBerdasarkanJenis(anggaran.getTipeAnggaran());
            kategoriCombo.getSelectionModel().select(anggaran.getKategori().getNamaKategori());
            showAlert("Kategori baru berhasil disimpan!");
        });
    }

    private void showOverallReportScreen() {
        laporanTextArea.setText(generateReportString());
        laporanTextArea.setEditable(false);
        Label reportTitleLabel = new Label("--- Laporan Keuangan Keseluruhan ---");
        reportTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Button printPreviewButton = new Button("Tampilkan Pratinjau Laporan");
        printPreviewButton.setMaxWidth(Double.MAX_VALUE);
        printPreviewButton.setOnAction(e -> showPrintPreview(generateReportString()));
        Button eksporCsvButton = new Button("Ekspor Laporan ke CSV (untuk Excel)");
        eksporCsvButton.setMaxWidth(Double.MAX_VALUE);
        eksporCsvButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        eksporCsvButton.setOnAction(e -> prosesEksporCsv());
        Button backButton = new Button("Kembali ke Menu Utama");
        backButton.setOnAction(e -> menampilkanFitur());
        backButton.setMaxWidth(Double.MAX_VALUE);
        VBox laporanLayout = new VBox(15, reportTitleLabel, laporanTextArea, printPreviewButton, eksporCsvButton, backButton);
        laporanLayout.setPadding(new Insets(20));
        laporanLayout.setAlignment(Pos.TOP_CENTER);
        Scene laporanScene = new Scene(laporanLayout, 700, 650);
        primaryStage.setTitle("Laporan Keuangan");
        primaryStage.setScene(laporanScene);
    }
    
    private void prosesEksporCsv() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("laporan_keuangan_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                String csvContent = generateTransaksiCSV();
                PrintWriter writer = new PrintWriter(file);
                writer.print(csvContent);
                writer.close();
                showAlert("Laporan berhasil diekspor ke:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Terjadi error saat mengekspor file:\n" + e.getMessage());
            }
        }
    }
    
    private String generateTransaksiCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID Transaksi,Tanggal,Waktu,Jenis,Kategori,Deskripsi,Jumlah\n");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        for (Transaksi trx : dataTransaksi) {
            csv.append(escapeCSV(trx.getId())).append(",");
            csv.append(escapeCSV(dateFormatter.format(trx.getTanggal()))).append(",");
            csv.append(escapeCSV(timeFormatter.format(trx.getTanggal()))).append(",");
            csv.append(escapeCSV(trx.getJenisTransaksi())).append(",");
            csv.append(escapeCSV(trx.getKategori().getNamaKategori())).append(",");
            csv.append(escapeCSV(trx.getDeskripsi())).append(",");
            csv.append(String.valueOf(trx.getJumlah())).append("\n");
        }
        return csv.toString();
    }
    
    private String escapeCSV(String data) {
        if (data == null) return "";
        if (data.contains(",") || data.contains("\"")) {
            data = data.replace("\"", "\"\"");
            return "\"" + data + "\"";
        }
        return data;
    }

    private void showPrintPreview(String reportText) {
        Stage printPreviewStage = new Stage();
        printPreviewStage.initOwner(primaryStage);
        printPreviewStage.initModality(Modality.APPLICATION_MODAL);
        TextArea previewTextArea = new TextArea(reportText);
        previewTextArea.setEditable(false);
        previewTextArea.setFont(Font.font("Monospaced", 12));
        VBox layout = new VBox(10, new Label("Pratinjau Laporan Keuangan"), previewTextArea);
        layout.setPadding(new Insets(10));
        Scene previewScene = new Scene(layout, 600, 700);
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
        jumlahField.clear();
        deskripsiField.clear();
        kategoriCombo.getSelectionModel().clearSelection();
    }
    
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
        laporan.append(String.format("ID Akun         : %s\n", akun.getIdAkun()));
        laporan.append(String.format("Nama Akun       : %s\n", akun.getNama()));
        laporan.append(String.format("Email Akun      : %s\n", akun.getEmail()));
        laporan.append(String.format("Saldo Saat Ini  : Rp %,.2f\n", saldoAkhir));
        laporan.append(String.format("Total Pemasukan : Rp %,.2f\n", totalPemasukan));
        laporan.append(String.format("Total Pengeluaran: Rp %,.2f\n", totalPengeluaran));
        laporan.append("\n--- Status Penggunaan Anggaran (Hanya Pengeluaran) ---\n");
        long jumlahAnggaranPengeluaran = akun.getAnggaranList().stream()
            .filter(a -> a.getTipeAnggaran().equalsIgnoreCase("Pengeluaran")).count();
        if (jumlahAnggaranPengeluaran == 0) {
            laporan.append("Tidak ada anggaran pengeluaran yang ditetapkan.\n");
        } else {
            for (Anggaran a : akun.getAnggaranList()) {
                if (a.getTipeAnggaran().equalsIgnoreCase("Pengeluaran")) {
                    double pengeluaranKategori = akun.getTransaksiList().stream()
                            .filter(t -> t.getJenisTransaksi().equalsIgnoreCase("Pengeluaran"))
                            .filter(t -> t.getKategori().getNamaKategori().equalsIgnoreCase(a.getKategori().getNamaKategori()))
                            .mapToDouble(Transaksi::getJumlah)
                            .sum();
                    laporan.append(String.format("Kategori: %-15s | Batas: Rp %-12s | Terpakai: Rp %s\n",
                            a.getKategori().getNamaKategori(), 
                            String.format("%,.2f", a.getBatas()), 
                            String.format("%,.2f", pengeluaranKategori)));
                }
            }
        }
        laporan.append(String.format("\n--- Jumlah Transaksi: %d ---\n", akun.getTransaksiList().size()));
        return laporan.toString();
    }
}