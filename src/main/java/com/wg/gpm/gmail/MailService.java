package com.wg.gpm.gmail;

import com.wg.gpm.message.RawMessage;

import java.util.List;

/**
 * Created by aidan on 01/11/16.
 */
public interface MailService {

    public List<RawMessage> pollForMessages();
}
