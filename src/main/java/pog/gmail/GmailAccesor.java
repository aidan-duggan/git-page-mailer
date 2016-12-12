package pog.gmail;

import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpEncoding;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import pog.message.RawMessage;

import java.io.IOException;
import java.util.*;

public class GmailAccesor implements  MailService{

    private static final String IS_UNREAD = "is:unread";
    private final MessageParser messageParser;
    private final Gmail service;
    private final String requiredSender;

    public GmailAccesor(Gmail service, String requiredSender){
        this.service = service;
        this.messageParser = new MessageParser(service);
        this.requiredSender = requiredSender;
    }

    @Override
    public List<RawMessage> pollForMessages() {
        try{
            return pollForMessagesInternal(this.requiredSender);
        }catch(Exception e){
            throw new IllegalStateException("polling for messages", e);
        }
    }

    private List<RawMessage> pollForMessagesInternal(String requiredSender) throws IOException {
        String user = "me";
        ListMessagesResponse listResponse =
                service.users().messages().list(user).setQ(IS_UNREAD).execute();
        List<Message> messages = listResponse.getMessages();
        if (messages == null || messages.size() == 0) {
            System.out.println("No new mails");
            return Collections.emptyList();
        } else {
            System.out.println("New mails");
            List<RawMessage> rawMessages = new ArrayList<>();
            for (Message message : messages) {
                Message realMessage = service.users().messages().get(user, message.getId())
                        .setFormat("FULL")
                        .execute();
                ModifyMessageRequest request = new ModifyMessageRequest().setRemoveLabelIds(Collections.singletonList("UNREAD"));
                service.users().messages().modify(user, message.getId(), request).execute();
                parseMessage(requiredSender, rawMessages, realMessage);
            }
            return rawMessages;
        }
    }

    private void parseMessage(String requiredSender, List<RawMessage> rawMessages, Message realMessage) throws IOException {
        Optional<MessagePartHeader> from = getFromField(realMessage);
        if(from.isPresent() && from.get().getValue().endsWith(requiredSender)) {
            System.out.println("Message found");

            rawMessages.add(messageParser.getContent(realMessage));
        }
    }

    private Optional<MessagePartHeader> getFromField(Message realMessage) {
        return realMessage.getPayload().getHeaders()
                .stream()
                .filter((header) -> header.getName().equals("From"))
                .findFirst();
    }

}