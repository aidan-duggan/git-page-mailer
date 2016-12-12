package pog.git;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import pog.message.PostDetails;

import java.io.File;
import java.io.IOException;

/**
 * Created by aidan on 24/10/16.
 */
public class DefaultGitAccess implements GitAccess{

    private static final String REMOTE_URL = "https://github.com/whilegaming/whilegaming.github.io.git";

    private final File gitDirectory;
    private final String gitToken;
    private final boolean pushToRemote;

    private DefaultGitAccess(File gitdirectory, String gitToken, boolean pushToRemote){
        super();
        this.gitDirectory = gitdirectory;
        this.gitToken = gitToken;
        this.pushToRemote = pushToRemote;
    }

    public static DefaultGitAccess buildGitAccess(Configuration config){
        String token = (String)config.getProperty("gittoken");
        String gitDirectory = (String)config.getProperty("gitdir");
        boolean pushToRemote = BooleanUtils.toBoolean((String)config.getProperty("pushToRemote"));
        return new DefaultGitAccess(new File(gitDirectory), token, pushToRemote);
    }

    private void syncRepo() throws GitAPIException {
        if(gitDirectory.exists()) {
            try {
                FileUtils.cleanDirectory(gitDirectory);
            } catch (IOException e) {
                throw new IllegalStateException("Cleaning " + gitDirectory, e);
            }
            gitDirectory.delete();
        }
        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + gitDirectory);
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(gitDirectory)
                .setProgressMonitor(new TextProgressMonitor())
                .call()) {
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            System.out.println("Having repository: " + result.getRepository().getDirectory());
        }
        System.out.println("done");
    }


    @Override
    public void createFileInGitAndCommit(PostDetails postDetails) {
        try {
            createFileInGitAndCommitInternal(postDetails);
            if(pushToRemote) {
                pushGitToRemote();
            }
        } catch (GitAPIException e) {
            throw new IllegalStateException("committing " + postDetails, e);
        }
    }

    private void createFileInGitAndCommitInternal(PostDetails postDetails) throws GitAPIException {
        try (Git git = Git.init().setDirectory(gitDirectory).call()) {

            // run the add
            git.add().addFilepattern(postDetails.getPostFilePattern())
                        .call();

            for(String attachments : postDetails.getAttachments()){
                git.add().addFilepattern(attachments).call();
            }

            // and then commit the changes
            git.commit()
                    .setMessage("Added file: " + postDetails)
                    .call();

            System.out.println("Committed file " + postDetails + " to repository at " + git.getRepository().getDirectory());
         }

    }

    private void pushGitToRemote() throws GitAPIException {
        try (Git git = Git.init().setDirectory(gitDirectory).call()) {

            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider( gitToken, "" ) ).call();

            System.out.println("Pushing to repository at " + git.getRepository().getDirectory());

        }

    }

    @Override
    public String getGitDirectory() {
        return this.gitDirectory.getAbsolutePath();
    }

    @Override
    public void syncIfMissing() throws GitAPIException {
        if(!this.gitDirectory.exists()){
            this.syncRepo();
        }
    }
}
