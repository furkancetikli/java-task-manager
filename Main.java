
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    // Kullanıcıdan güvenli şekilde tam sayı alır.
    public static int guvenliSayiAl(Scanner scanner, String mesaj) {
        while (true) {
            System.out.println(mesaj);
            String giris = scanner.nextLine().trim();

            try {
                return Integer.parseInt(giris);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Hatalı giriş! Lütfen geçerli bir tam sayı girin."
                );
            }
        }
    }

    // Görevleri dosyaya kaydeder.
    public static void gorevleriKaydet(ArrayList<Task> tasks) {
        try (FileWriter writer = new FileWriter("tasks.txt")) {

            for (Task task : tasks) {
                writer.write(
                        task.name + ";" + task.completed
                                + System.lineSeparator()
                );
            }

        } catch (IOException e) {
            System.out.println("Görevler dosyaya kaydedilemedi.");
        }
    }

    // Dosyadan görevleri yükler.
    public static void gorevleriYukle(
            ArrayList<Task> tasks, Path dosyaYolu) {

        try {
            if (Files.exists(dosyaYolu)) {

                for (String satir : Files.readAllLines(dosyaYolu)) {

                    String[] bilgiler = satir.split(";", 2);
                    Task task = new Task(bilgiler[0]);

                    if (bilgiler.length > 1) {
                        task.completed =
                                Boolean.parseBoolean(bilgiler[1]);
                    }

                    tasks.add(task);
                }

            } else {
                tasks.add(new Task("Java öğren"));
                tasks.add(new Task("Kitap oku"));
                tasks.add(new Task("Spor yap"));

                gorevleriKaydet(tasks);
            }

        } catch (IOException e) {
            System.out.println("Görevler dosyadan okunamadı.");
        }
    }

    // Görevleri numaralı olarak gösterir.
    public static void gorevleriGoster(ArrayList<Task> tasks) {

        if (tasks.isEmpty()) {
            System.out.println("Görev listeniz boş.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            String durum = task.completed
                    ? "Tamamlandı"
                    : "Tamamlanmadı";

            System.out.println(
                    (i + 1) + ". " + task.name + " | " + durum
            );
        }
    }

    // Kullanıcıdan mevcut görevlerden birinin numarasını alır.
    public static int gorevNumarasiAl(
            Scanner scanner, ArrayList<Task> tasks, String mesaj) {

        while (true) {
            int numara = guvenliSayiAl(scanner, mesaj);

            if (numara >= 1 && numara <= tasks.size()) {
                return numara;
            }

            System.out.println(
                    "Geçersiz görev numarası! 1 ile "
                            + tasks.size() + " arasında bir sayı girin."
            );
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        Path dosyaYolu = Path.of("tasks.txt");

        gorevleriYukle(tasks, dosyaYolu);

        while (true) {

            System.out.println("\n=== GÖREV YÖNETİMİ ===");
            System.out.println("1. Görevleri Göster");
            System.out.println("2. Yeni Görev Ekle");
            System.out.println("3. Görev Sil");
            System.out.println("4. Görevi Tamamla");
            System.out.println("0. Çıkış");

            int secim = guvenliSayiAl(
                    scanner, "Seçiminizi girin:"
            );

            if (secim == 1) {

                gorevleriGoster(tasks);

            } else if (secim == 2) {

                System.out.println("Yeni görevi giriniz:");
                String yeniGorev = scanner.nextLine().trim();

                if (yeniGorev.isEmpty()) {
                    System.out.println(
                            "Görev adı boş olamaz!"
                    );
                    continue;
                }

                // Dosya formatımızda noktalı virgül ayraçtır.
                if (yeniGorev.contains(";")) {
                    System.out.println(
                            "Görev adında ';' karakteri kullanmayın."
                    );
                    continue;
                }

                tasks.add(new Task(yeniGorev));
                gorevleriKaydet(tasks);

                System.out.println(
                        "Yeni görev başarıyla eklendi!"
                );

            } else if (secim == 3) {

                if (tasks.isEmpty()) {
                    System.out.println(
                            "Görev listeniz boş; silinecek görev yok."
                    );
                    continue;
                }

                gorevleriGoster(tasks);

                int numara = gorevNumarasiAl(
                        scanner,
                        tasks,
                        "Silmek istediğiniz görevin numarasını girin:"
                );

                tasks.remove(numara - 1);
                gorevleriKaydet(tasks);

                System.out.println(
                        "Görev başarıyla silindi."
                );

            } else if (secim == 4) {

                if (tasks.isEmpty()) {
                    System.out.println(
                            "Görev listeniz boş."
                    );
                    continue;
                }

                gorevleriGoster(tasks);

                int numara = gorevNumarasiAl(
                        scanner,
                        tasks,
                        "Tamamladığınız görevin numarasını girin:"
                );

                Task task = tasks.get(numara - 1);

                if (task.completed) {
                    System.out.println(
                            "Bu görev zaten tamamlanmış."
                    );
                } else {
                    task.completed = true;
                    gorevleriKaydet(tasks);

                    System.out.println(
                            "Görev tamamlandı olarak işaretlendi!"
                    );
                }

            } else if (secim == 0) {

                System.out.println("Program kapatılıyor...");
                break;

            } else {

                System.out.println(
                        "Geçersiz seçim! 0 ile 4 arasında bir seçim yapın."
                );
            }
        }

        scanner.close();
    }
}
