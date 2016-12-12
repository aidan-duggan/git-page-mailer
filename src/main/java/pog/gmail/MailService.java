package pog.gmail;

import pog.message.RawMessage;

import java.util.List;

/**
 * Created by aidan on 01/11/16.
 */
public interface MailService {

    public List<RawMessage> pollForMessages();
}
