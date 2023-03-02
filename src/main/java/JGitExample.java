import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class JGitExample {
	public static void main(String[] args) throws IOException, GitAPIException {
		// Specify the path to the file you want to version
		String filePath = "/tmp/jgit";
		new File(filePath).mkdirs();

		// Initialize a new repository at the file's location
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(filePath + "/.git"))
			.readEnvironment() // scan environment GIT_* variables
			.findGitDir() // scan up the file system tree
			.build();

		// Create a new Git object from the repository
		Git git = new Git(repository);

		// Add the file to the repository
		git.add().addFilepattern(".").call();

		// Commit the changes to the repository
		git.commit().setMessage("Initial commit").call();

		// Print out the commit ID
		System.out.println("Committed file " + filePath + " to repository " + repository.getDirectory());
		System.out.println("Commit ID: " + repository.resolve("HEAD").name());

		// Close the Git object and repository
		git.close();
		repository.close();
	}
}
