# Enygma: Anonymous Social Journal App

## 📝 Deskripsi Aplikasi

Enygma adalah aplikasi Android yang dirancang sebagai platform sosial media anonim, memungkinkan pengguna untuk berbagi pikiran, perasaan, dan pengalaman harian mereka tanpa perlu mengungkapkan identitas asli. Aplikasi ini menyediakan ruang yang aman dan mendukung bagi pengguna untuk mengekspresikan diri dan terhubung dengan komunitas yang memahami. Selain fitur berbagi postingan, Enygma juga menawarkan fitur jurnal pribadi untuk mencatat pengalaman harian yang lebih intim.

**Fitur Utama:**

* **Postingan Anonim:** Pengguna dapat membuat dan melihat postingan curhat anonim yang dikelompokkan berdasarkan kategori dan mood.
* **Dukungan (Support):** Berikan dukungan virtual pada postingan orang lain.
* **Komentar Anonim:** Berinteraksi dengan postingan melalui komentar tanpa mengungkapkan identitas.
* **Jurnal Harian:** Fitur khusus untuk membuat, melihat detail, mengedit, dan menghapus catatan jurnal pribadi harian.
* **Sistem Autentikasi (Lokal):** Login dan registrasi pengguna dengan simulasi sesi menggunakan token (tanpa reset password).
* **Landing Page (First-Time User):** Tampilan pembuka aplikasi yang hanya muncul saat pertama kali diinstal.
* **Dukungan Offline:** Semua data postingan, komentar, dan jurnal disimpan secara lokal menggunakan database SQLite, memastikan akses dan fungsionalitas penuh bahkan tanpa koneksi internet.
* **Tema Adaptif:** Beralih antara mode terang (Light Theme) dan gelap (Dark Theme) untuk pengalaman pengguna yang lebih nyaman.
* **Kutipan Inspirasi Online:** Dapatkan kutipan acak dari API eksternal (ZenQuotes.io) untuk motivasi harian.

## 🚀 Cara Penggunaan

Ikuti langkah-langkah di bawah ini untuk menjalankan aplikasi Enygma di perangkat Android atau emulator Anda:

### Prasyarat

* Android Studio Arctic Fox atau yang lebih baru.
* JDK 11 atau yang lebih baru.
* Perangkat Android (min API 24) atau Emulator.

### Instalasi dan Menjalankan Aplikasi

1.  **Clone Repositori:**
    ```bash
    git clone [https://github.com/nabilmuflif/proyek](https://github.com/nabilmuflif/proyek)
    ```

2.  **Buka Proyek di Android Studio:**
    * Buka Android Studio.
    * Pilih `File` > `Open` atau `Open an Existing Project`.
    * Navigasi ke folder `curhatku-app` (atau nama repositori Anda) yang baru Anda *clone* dan klik `OK`.
3.  **Sinkronisasi Gradle:**
    * Biarkan Android Studio melakukan sinkronisasi Gradle secara otomatis. Pastikan Anda terhubung ke internet untuk mengunduh dependensi.
4.  **Jalankan Aplikasi:**
    * Pilih perangkat Android atau emulator dari dropdown di toolbar Android Studio.
    * Klik tombol `Run 'app'` (ikon segitiga hijau).

### Alur Aplikasi

1.  **Login/Register:**
    * Jika ini pertama kali, atau Anda belum login, Anda akan diarahkan ke layar Login.
    * Gunakan tombol "Register" untuk membuat akun baru (contoh: `username: user`, `password: pass`).
    * Setelah register, gunakan kredensial tersebut untuk "Login".
2.  **Home (Curhat):**
    * Setelah login, Anda akan melihat daftar postingan curhat.
    * Anda dapat mengklik postingan untuk melihat detail dan komentar.
    * Gunakan Floating Action Button (FAB) `+` di kanan bawah untuk membuat postingan curhat baru.
3.  **Jurnal:**
    * Pilih tab "Jurnal" di Bottom Navigation.
    * Anda akan melihat daftar catatan jurnal harian Anda.
    * Gunakan Floating Action Button (FAB) `+` di kanan bawah untuk membuat catatan jurnal baru.
    * Klik item jurnal untuk melihat detail, mengedit, atau menghapusnya.
4.  **Profil:**
    * Pilih tab "Profil" di Bottom Navigation.
    * Lihat kutipan inspirasi (memerlukan koneksi internet).
    * Ubah tema (mode gelap/terang).
    * Gunakan "Edit Profil" untuk mengubah username dan email.
    * Gunakan "Ganti Password" untuk mengubah password lama dengan password baru (membutuhkan konfirmasi password lama).
    * Hapus semua data lokal.
    * Logout dari akun Anda.

## 💻 Implementasi Teknis Singkat

Proyek ini dibangun menggunakan **Android Native (Java)** dan mengikuti arsitektur modular yang rapi.

* **Komponen Dasar Android:**
    * **Activities:**  `LoginActivity`, `RegisterActivity` `MainActivity`, `PostDetailActivity` untuk mengelola interaksi pengguna utama. Aplikasi harus memiliki minimal dua Activity yang berbeda. [cite_start]Salah satu Activity harus menjadi Launcher aplikasi (MainActivity).
    * **Fragments:** `HomeFragment`, `CreatePostFragment`, `ProfileFragment`, `JournalListFragment`, `CreateJournalFragment`, `JournalDetailFragment` untuk modularitas UI dan navigasi. [cite_start]Aplikasi harus memiliki minimal dua Fragment.
    * **Intents:** Digunakan untuk berpindah antar Activity dan meneruskan data. [cite_start]Implementasikan penggunaan Intent untuk berkomunikasi dan berpindah antar Activity.

* **Navigasi:**
    * Menggunakan **Android Jetpack Navigation Component** (`mobile_navigation.xml`) untuk mengelola navigasi antar fragment, termasuk transisi dan argument passing. [cite_start]Gunakan Navigation Component untuk mengelola navigasi antar Fragment.
    * **BottomNavigationView** terintegrasi dengan Navigation Component untuk navigasi utama aplikasi.

* **Penyimpanan Data Lokal:**
    * **SQLite Database:** Data utama (Postingan, Komentar, Pengguna, Jurnal) disimpan secara lokal menggunakan implementasi SQLite manual (`DatabaseHelper.java`). Ini memastikan persistensi data dan fungsionalitas offline. [cite_start]Gunakan SQLite atau SharedPreferences untuk menyimpan data secara lokal. [cite_start]Data yang disimpan harus dapat ditampilkan kembali ketika aplikasi tidak terhubung ke jaringan.
    * **SharedPreferences:** Digunakan untuk menyimpan preferensi pengguna (seperti tema) dan status sesi (login, first-time user). [cite_start]Gunakan SQLite atau SharedPreferences untuk menyimpan data secara lokal.

* **Manajemen Sesi & Autentikasi:**
    * Kelas `SessionManager.java` mengelola status login dan token autentikasi (disimpan di `SharedPreferences`).
    * Login dan registrasi diverifikasi secara lokal terhadap database SQLite (`User` tabel) untuk mensimulasikan proses autentikasi.

* **Networking:**
    * **Retrofit:** Digunakan untuk melakukan panggilan API ke layanan eksternal (`QuoteApiService`). [cite_start]Implementasikan fungsi untuk mengambil data dari API menggunakan Retrofit.
    * **OkHttp Logging Interceptor:** Digunakan untuk *logging* permintaan dan respons HTTP untuk *debugging*.
    * **API Kutipan:** Mengambil kutipan dari `https://zenquotes.io/api/random`. [cite_start]Data yang diambil harus ditampilkan di aplikasi.
    * **Penanganan Offline/Error:** `NetworkManager` secara cerdas mendeteksi status jaringan dan menangani kegagalan API dengan menampilkan pesan error informatif (tidak ada *mock data* yang ditampilkan dari API jika gagal). [cite_start]Aplikasi memiliki tombol refresh saat gagal menampilkan data dari API (kondisi tidak ada jaringan).

* **Background Processing:**
    * Semua operasi database dan jaringan dijalankan di **`ExecutorService`** (`background thread`) untuk menjaga UI tetap responsif. [cite_start]Aplikasi harus menjalankan operasi di latar belakang menggunakan Executor maupun Handler.
    * `Handler` digunakan untuk memperbarui UI (`main thread`) setelah operasi di latar belakang selesai. [cite_start]Aplikasi harus menjalankan operasi di latar belakang menggunakan Executor maupun Handler.

* **Desain & Tema:**
    * Mengikuti prinsip **Material Design 3** untuk tampilan dan nuansa modern.
    * Mendukung dua tema: terang dan gelap, yang dapat diubah melalui `ProfileFragment` menggunakan `ThemeManager` dan konfigurasi `themes.xml` yang sesuai. [cite_start]Aplikasi harus menerapkan dua tema (dark theme / light theme).

---

**Catatan untuk Penilai:**

* [cite_start]Proyek ini telah dikembangkan dengan fokus pada pemenuhan spesifikasi teknis Lab Mobile 2025.
* Fitur autentikasi (login/register) adalah simulasi lokal, bukan terhubung ke backend server nyata.
* Fungsionalitas "Edit Profile" dan "Ganti Password" di `ProfileFragment` adalah implementasi lokal yang memodifikasi data di database SQLite.
* Semua perubahan telah di-*commit* dengan pesan semantik yang berurutan.
* [cite_start]Dokumentasi ini mencakup deskripsi aplikasi, cara penggunaan, dan penjelasan singkat tentang implementasi teknis.

---
