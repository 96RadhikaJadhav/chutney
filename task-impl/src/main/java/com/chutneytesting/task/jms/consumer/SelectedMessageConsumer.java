package com.chutneytesting.task.jms.consumer;

import com.chutneytesting.task.jms.consumer.bodySelector.BodySelector;
import com.chutneytesting.tools.Streams;
import java.util.Enumeration;
import java.util.Optional;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;

class SelectedMessageConsumer implements Consumer {

    private final QueueBrowser browser;
    private final BodySelector bodySelector;
    private final int browserMaxDepth;

    SelectedMessageConsumer(QueueBrowser browser, BodySelector bodySelector, int browserMaxDepth) {
        this.browser = browser;
        this.bodySelector = bodySelector;
        this.browserMaxDepth = browserMaxDepth;
    }

    @SuppressWarnings("unchecked")
    public Optional<Message> getMessage() throws JMSException {
        Enumeration<Message> messageEnumeration = browser.getEnumeration();
        return Streams.toStream(messageEnumeration)
            .limit(browserMaxDepth)
            .filter(bodySelector::match)
            .findFirst();
    }
}
