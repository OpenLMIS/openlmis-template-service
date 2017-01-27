package org.openlmis.template.security;

import org.javers.spring.auditable.AuthorProvider;

/**
 * This class is used by JaVers to retrieve the name of the user currently logged in.
 * JaVers then associates audited changes being made with this particular user.
 */
public class UserNameProvider implements AuthorProvider {

  /**
   * A service intended for production would offer an implementation of
   * provide() based on its approach to authentication.
   */
  @Override
  public String provide() {
   return "unauthenticated user";
  }

}
