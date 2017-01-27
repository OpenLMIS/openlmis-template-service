package org.openlmis.template.web;

import org.apache.commons.lang3.StringUtils;
import org.javers.core.Javers;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.diff.Change;
import org.javers.core.json.JsonConverter;
import org.javers.repository.jql.QueryBuilder;
import org.openlmis.template.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api")
public abstract class BaseController {

  @Autowired
  private Javers javers;

  /**
   * <p>
   * Convenience method intended to return audit log information via either JSON or raw text,
   * based on the value of the returnJson argument.
   * </p><p>
   * For testing and development, it’s useful to set this to false in order to retrieve a
   * human-readable response. For production, however, JSON should exclusively be returned.
   * </p>
   * See getAuditedChanges() for a list and explanation of the available parameters.
   */
  protected String getAuditLog(Class type, UUID id, String author, String changedPropertyName,
                               Pageable page, boolean returnJson) {
    if (returnJson) {
      return getAuditLogJson(type, id, author, changedPropertyName, page);
    } else {
      return getAuditLogText(type, id, author, changedPropertyName, page);
    }
  }

  /**
   * Return a list of audited changes via JSON.
   * @param type The type of class for which we wish to retrieve historical changes.
   */
  protected String getAuditLogJson(Class type) {
    return getAuditLogJson(type, null, null, null, null);
  }

  protected String getAuditLogJson(Class type, UUID id) {
    return getAuditLogJson(type, id, null, null, null);
  }

  /**
   * Return a list of changes via JSON.
   *
   * @param type The type of class for which we wish to retrieve historical changes.
   * @param id The ID of class for which we wish to retrieve historical changes.
   *           If null, entries are returned regardless of their ID.
   * @param author The author of the changes which should be returned.
   *               If null or empty, changes are returned regardless of author.
   * @param changedPropertyName The name of the property about which changes should be returned.
   *               If null or empty, changes associated with any and all properties are returned.
   * @param page A Pageable object with PageNumber and PageSize values used for pagination.
   */
  protected String getAuditLogJson(Class type, UUID id, String author,
                                     String changedPropertyName, Pageable page) {
    List<Change> changes = getChangesByType(type, id, author, changedPropertyName, page);
    JsonConverter jsonConverter = javers.getJsonConverter();
    return jsonConverter.toJson(changes);
  }


  /**
   * Return a list of changes as a log (in other words, as a series of line entries).
   * @param type The type of class for which we wish to retrieve historical changes.
   */
  protected String getAuditLogText(Class type) {
    return getAuditLogText(type, null, null, null, null);
  }

  /**
   * Return a list of changes as a log (in other words, as a series of line entries).
   * @param type The type of class for which we wish to retrieve historical changes.
   * @param id The ID of class for which we wish to retrieve historical changes.
   *           If null, entries are returned regardless of their ID.
   */
  protected String getAuditLogText(Class type, UUID id) {
    return getAuditLogText(type, id, null, null, null);
  }

  /**
   * Return a list of changes as a log (in other words, as a series of line entries).
   * The available parameters and their means are the same as for the getChangesByClass() method.
   */
  protected String getAuditLogText(Class type, UUID id, String author,
                                   String changedPropertyName, Pageable page) {
    List<Change> changes = getChangesByType(type, id, author, changedPropertyName, page);
    return javers.processChangeList(changes, new SimpleTextChangeLog());
  }


  /*
    Return JaVers changes for the specified type, optionally filtered by id, author, and property.
  */
  private List<Change> getChangesByType(Class type, UUID id, String author,
                                        String changedPropertyName, Pageable page) {
    QueryBuilder queryBuilder;

    if (id != null) {
      queryBuilder = QueryBuilder.byInstanceId(id, type);
    } else {
      queryBuilder = QueryBuilder.byClass(type);
    }

    int skip = Pagination.getPageNumber(page);
    int limit = Pagination.getPageSize(page);

    queryBuilder = queryBuilder.withNewObjectChanges(true).skip(skip).limit(limit);

    if ( StringUtils.isNotBlank(author) ) {
      queryBuilder = queryBuilder.byAuthor(author);
    }
    if ( StringUtils.isNotBlank(changedPropertyName) ) {
      queryBuilder = queryBuilder.andProperty(changedPropertyName);
    }

    /* Depending on the business' preference, we can either use findSnapshots() or findChanges().
       Whereas the former returns the entire state of the object as it was at each commit, the later
       returns only the property and values which changed. */
    //List<Change> changes = javers.findSnapshots(queryBuilder.build());
    List<Change> changes = javers.findChanges(queryBuilder.build());

    changes.sort((o1, o2) -> -1 * o1.getCommitMetadata().get().getCommitDate()
                                  .compareTo(o2.getCommitMetadata().get().getCommitDate()));
    return changes;
  }

}
