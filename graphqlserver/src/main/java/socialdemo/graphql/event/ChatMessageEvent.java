package socialdemo.graphql.event;

public class ChatMessageEvent {
    private String sender;
    private String receiver;
    private String encryptedContent;
    private String venueId;
    private long timestamp;

    public ChatMessageEvent() {
        // Default constructor for Kafka deserialization
    }

    public ChatMessageEvent(String sender, String receiver, String encryptedContent, String venueId) {
        this.sender = sender;
        this.receiver = receiver;
        this.encryptedContent = encryptedContent;
        this.venueId = venueId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
