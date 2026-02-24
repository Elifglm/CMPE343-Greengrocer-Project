package model;

import java.sql.Timestamp;

/**
 * Message entity for customer-owner communication.
 * 
 * INHERITANCE: Extends Entity
 */
public class Message extends Entity {

    private String senderUsername;
    private String receiverUsername;
    private String subject;
    private String content;
    private boolean isRead;
    private Integer parentMessageId;
    private Timestamp sentAt;

    public Message() {
        super();
    }

    public Message(int messageId, String senderUsername, String receiverUsername,
            String subject, String content, boolean isRead,
            Integer parentMessageId, Timestamp sentAt) {
        super(messageId);
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.subject = subject;
        this.content = content;
        this.isRead = isRead;
        this.parentMessageId = parentMessageId;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Integer getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Integer parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String getDisplayName() {
        return "From: " + senderUsername + " - " + subject;
    }
}
