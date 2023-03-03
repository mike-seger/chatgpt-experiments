import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.storage.file.*;
import org.eclipse.jgit.transport.*;
import java.io.*;
import java.util.zip.*;

public class JGitExample {

    private static final String TIMESTAMP_FILE = "timestamp.txt";

    public static void main(String[] args) throws IOException, GitAPIException {
        if (args.length == 0) {
            System.out.println("Please provide path to zip file containing the git repository");
            return;
        }

        File zipFile = new File(args[0]);
        File unzipDir = new File("/tmp/new");

        if (!zipFile.exists()) {
            System.out.println("Zip file does not exist");
            return;
        }

        if (!unzipDir.exists()) {
            unzipDir.mkdirs();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                File file = newFile(unzipDir, zipEntry);

                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        }

        File repoDir = new File(unzipDir, zipFile.getName().replaceAll(".zip", ""));

        try (Git git = Git.open(repoDir)) {
            // Add or modify timestamp.txt
            File timestampFile = new File(repoDir, TIMESTAMP_FILE);
            if (timestampFile.exists()) {
                // File already exists, modify it
                try (FileWriter fileWriter = new FileWriter(timestampFile)) {
                    fileWriter.write(String.valueOf(System.currentTimeMillis()));
                }
            } else {
                // File doesn't exist, create it
                timestampFile.createNewFile();
                try (FileWriter fileWriter = new FileWriter(timestampFile)) {
                    fileWriter.write(String.valueOf(System.currentTimeMillis()));
                }
                git.add().addFilepattern(TIMESTAMP_FILE).call();
            }

            // Commit changes
            git.commit().setMessage("Added or modified " + TIMESTAMP_FILE).call();

            // Zip repository
            File newZipFile = new File(zipFile.getAbsolutePath());
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(newZipFile))) {
                zipDir(repoDir, repoDir, zipOutputStream);
            }
        }

        System.out.println("Done!");
    }

    private static void zipDir(File directoryToZip, File rootDirectory, ZipOutputStream zipOutputStream) throws IOException {
        File[] children = directoryToZip.listFiles();
        for (File file : children) {
            if (file.isDirectory()) {
                zipDir(file, rootDirectory, zipOutputStream);
            } else {
                String relativePath = file.getAbsolutePath().substring(rootDirectory.getAbsolutePath().length() + 1);
                ZipEntry zipEntry = new ZipEntry(relativePath);
                zipOutputStream.putNextEntry(zipEntry);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                }
                zipOutputStream.closeEntry();
            }
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: "+destFilePath);
        }
        return destFile;
     }
}
   
