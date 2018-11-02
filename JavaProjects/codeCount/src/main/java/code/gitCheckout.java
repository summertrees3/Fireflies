package code;

import java.io.File;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class gitCheckout {
	
//	private final Logger logger = LoggerFactory.getLogger(gitCheckout.class);
	
	public static void main(String[] args) {
		
		File file = new File("E:\\resume-system");
		String version = "0e03f7a90aaf8e333fe4be36f5821fc38f8162be";
		
	    File RepoGitDir = new File(file.getAbsolutePath() + "/.git");
	    if (!RepoGitDir.exists()) {
//	        logger.info("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
	    } else {
	        Repository repo = null;
	        try {
	            repo = new FileRepository(RepoGitDir.getAbsolutePath());
	            Git git = new Git(repo);
	            CheckoutCommand checkout = git.checkout();
	            checkout.setName(version);
	            checkout.call();

	            PullCommand pullCmd = git.pull();
	            pullCmd.call();
	            
//	            CommitCommand commitCmd = git.commit();
//	            commitCmd.call();

//	            logger.info("Pulled from remote repository to local repository at " + repo.getDirectory());
	        } catch (Exception e) {
//	            logger.info(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
	        } finally {
	            if (repo != null) {
	                repo.close();
	            }
	        }
	    }
	}
}
