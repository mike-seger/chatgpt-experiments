import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class JGitExample {
    public static void main(String[] args) throws IOException, GitAPIException {
        // Check that the correct number of arguments were passed
        if (args.length != 1) {
            System.out.println("Usage: JGitExample <path to zip file>");
            return;
        }

        // Get the path to the zip file
        String zipFilePath = args[0];

        // Unzip the repository to /tmp/new
        Path tmpDir = Files.createTempDirectory("new");
        unzip(zipFilePath, tmpDir.toString());

        // Get the path to the unzipped repository
        String repoPath = tmpDir.resolve("repo.git").toString();

        // Initialize a new repository at the unzipped repository's location
        Repository repository = FileRepositoryBuilder.create(new File(repoPath));

        // Create a new Git object from the repository
        Git git = new Git(repository);

        // Create an index for the bare repository
        DirCache index = DirCache.newInCore();
        index.lock();
        repository.writeDirCache(index);

        // Add the file to the repository
        git.add().addFilepattern(".").call();

        // Commit the changes to the repository
        git.commit().setMessage("Initial commit").call();

        // Retrieve the HEAD commit
        String head = repository.resolve("HEAD").getName();

        // Print out the commit ID
        System.out.println("Committed files to repository " + repository.getDirectory());
        System.out.println("HEAD commit ID: " + head);

        // Close the Git object and repository
        git.close();
        repository.close();
    }

    private static void unzip(String zipFilePath, String destDir) throws IOException {
        File dest = new File(destDir);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(dest, zipEntry);
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                newFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
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
