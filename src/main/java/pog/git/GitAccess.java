package pog.git;

import org.eclipse.jgit.api.errors.GitAPIException;
import pog.message.PostDetails;

/**
 * Created by aidan on 31/10/16.
 */
public interface GitAccess {

    public void createFileInGitAndCommit(PostDetails files);

    public String getGitDirectory();

    public void syncIfMissing() throws GitAPIException;
}
