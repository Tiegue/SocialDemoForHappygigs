package socialdemo.graphql.model;

public record Message(
        String sender,
        String receiver,
        String content,
        String timestamp,
        VisitType visitType
) {}
