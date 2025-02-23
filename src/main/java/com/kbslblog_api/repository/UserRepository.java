package com.kbslblog_api.repository;

import com.kbslblog_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
 * Retrieves a user entity by its login identifier.
 *
 * @param loginId the login identifier of the user to retrieve
 * @return an Optional containing the User if found, or an empty Optional if no user exists with the given loginId
 */
Optional<User> findByLoginId(String loginId);

    /**
 * Retrieves a User entity matching the given login ID and email.
 *
 * <p>This method returns an Optional containing the User if a match is found, or an empty Optional otherwise.
 *
 * @param loginId the user's login identifier
 * @param email the user's email address
 * @return an Optional containing the matched User, or empty if no matching user exists
 */
Optional<User> findByLoginIdAndEmail(String loginId, String email);

    /**
 * Retrieves a user by matching both the login identifier and phone number.
 *
 * @param loginId the login identifier to search for
 * @param phoneNumber the phone number to search for
 * @return an Optional containing the matching user if found, or an empty Optional otherwise
 */
Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

    /**
 * Checks whether a user with the specified email exists.
 *
 * @param email the email address to check for user existence
 * @return true if a user with the given email exists, otherwise false
 */
boolean existsByEmail(String email);

    /**
 * Determines if a user exists with the specified phone number.
 *
 * @param phoneNumber the phone number to check for an associated user.
 * @return true if a user with the provided phone number exists, false otherwise.
 */
boolean existsByPhoneNumber(String phoneNumber);

    /**
 * Checks if a user exists with the specified login identifier.
 *
 * @param loginId the login identifier to check for existence
 * @return true if a user with the given login identifier exists, false otherwise
 */
boolean existsByLoginId(String loginId);
}
