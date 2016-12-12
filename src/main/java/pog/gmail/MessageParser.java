package pog.gmail;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import pog.message.Attachment;
import pog.message.RawMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by aidan on 31/10/16.
 */
class MessageParser {

    private final Gmail service;

    public MessageParser(Gmail service) {
        this.service = service;
    }

    RawMessage getContent(Message message) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        readPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
        byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
        List<Attachment> attachments = getAttachments(message);
        return new RawMessage(buildTitle(message), new String(bodyBytes), attachments);
    }

    private String buildTitle(Message message) {
        Optional<MessagePartHeader> subject = getSubjectField(message);
        if(subject.isPresent()){
            return subject.get().getValue();
        }else{
            return "NoTitle";
        }
    }

    private void readPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder stringBuilder) {
        for (MessagePart messagePart : messageParts) {
            if (messagePart.getMimeType().equals("text/plain")) {
                stringBuilder.append(messagePart.getBody().getData());
            }

            if (messagePart.getParts() != null) {
                readPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
            }
        }
    }

    private Optional<MessagePartHeader> getSubjectField(Message realMessage) {
        return realMessage.getPayload().getHeaders()
                .stream()
                .filter((header) -> header.getName().equals("Subject"))
                .findFirst();
    }

    private List<Attachment> getAttachments(Message message) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        List<MessagePart> parts = message.getPayload().getParts();
        for (MessagePart part : parts) {
            if (part.getFilename() != null && part.getFilename().length() > 0) {
                String filename = part.getFilename();
                String attId = part.getBody().getAttachmentId();
                MessagePartBody attachPart = service.users().messages().attachments().
                        get("me", message.getId(), attId).execute();

                byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
                attachments.add(new Attachment(filename, fileByteArray));
            }
        }
        return attachments;
    }
}
