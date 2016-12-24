package com.wg.gpm.message;

import static com.wg.gpm.properties.Property.*;

import com.wg.gpm.parser.DefaultParserContext;
import com.wg.gpm.parser.LineParser;
import com.wg.gpm.parser.ParserContext;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by aidan on 31/10/16.
 */
public class MessageFileWriter {

    private final String gitDirectory;
    private final LineParser lineParser;

    public MessageFileWriter(String gitDirectory, LineParser lineParser){
        this.gitDirectory = gitDirectory;
        this.lineParser = lineParser;
    }

    public Optional<PostDetails> writeMessageToFile(RawMessage message){
        StringReader stringReader = new StringReader(message.getBody());
        try (BufferedReader bufReader = new BufferedReader(stringReader)){
            logOriginalMessage(message.getBody());
            String fileName = buildPostName(message);

            String postPattern = buildPostPattern(fileName);
            File postFile = getFile(postPattern);
            List<String> attachments = writeAttachmentsToFile(message.getAttachments());

            PostDetails postDetails = new PostDetails(postPattern, attachments);
            writeLinesToFile(bufReader, postFile, postDetails);

            return Optional.of(postDetails);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void logOriginalMessage(String body) throws IOException {
        File file = new File("target/" + System.currentTimeMillis() + ".txt");
        if(file.createNewFile()) {
            writeToFile(body.getBytes(), file);
        }else{
            throw new IOException("temp file already exists");
        }
    }

    private String buildPostPattern(String fileName) {
        return POST_DIR + "/" + fileName + MD;
    }

    private String buildPostName(RawMessage message) {
        return LocalDate.now().toString() + "-" + message.getTitle();
    }

    private List<String> writeAttachmentsToFile(List<Attachment> attachments) throws IOException {
        List<String> attachmentFiles = new ArrayList<>(attachments.size());
        for(Attachment attachment : attachments){
            byte[] contents = attachment.getContents();
            String attachmentPattern = buildImagePattern(attachment);
            File file = getFile(attachmentPattern);
            writeToFile(contents, file);
            attachmentFiles.add(attachmentPattern);
        }
        return attachmentFiles;
    }

    private void writeToFile(byte[] contents, File file) throws IOException {
        FileOutputStream fileOutFile =
                new FileOutputStream(file);
        fileOutFile.write(contents);
        fileOutFile.close();
    }

    private String buildImagePattern(Attachment attachment) {
        return IMG_DIR + "/" + attachment.getTitle();
    }

    private void writeLinesToFile(BufferedReader bufReader, File postFile, PostDetails postDetails) throws IOException {
        ParserContext context = new DefaultParserContext(postDetails);
        Deque<String> queue = new ArrayDeque<String>();
        bufReader.lines().forEach(queue::push);
        Collection<String> result = lineParser.parse(context, queue);
        Files.write(postFile.toPath(), (Iterable<String>) result::iterator);
    }

    private File getFile(String fileName) throws IOException {
        File file = new File(gitDirectory + "/" + fileName);
        if(!file.exists()){
            if(!file.createNewFile()){
                throw new IllegalStateException(file.getAbsolutePath() + " already exists");
            }
        }
        return file;
    }
}
