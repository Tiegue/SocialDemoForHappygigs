package socialdemo.graphql.model;

import java.util.Set;

/**
 * A payload used for emitting a user list to a specific user via GraphQL subscriptions.
 * <p>
 * This record is typically used in combination with a shared {@code Sinks.Many<UserListPayload>}
 * so that the backend can emit a list of users intended for one specific new user
 * (e.g., when they enter a venue).
 * </p>
 *
 * <p>
 * This avoids broadcasting user lists to all users and keeps venue presence updates scoped
 * only to the relevant recipient.
 * </p>
 *
 * @param userId the user ID of the subscriber who should receive this user list
 * @param users the set of user IDs representing the current users in the venue (excluding the new user)
 */
public record UserListPayload (String userId, Set<String> users) {
}
