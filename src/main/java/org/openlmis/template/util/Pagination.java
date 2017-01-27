package org.openlmis.template.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class Pagination {

  /*
    Because Spring itself uses 0 as the default pageNumber, this value probably shouldn't be changed
   */
  public static final int DEFAULT_PAGE_NUMBER = 0;

  /* Note that, even though we set this to Integer.MAX_VALUE, Spring is configured to enforce a
    default of no more that 2000. We can use the below property to effectively set any value less
    than 2000. If we want a larger value, however, it seems as though it'll be necessary to include
    Spring Data Rest and to set the value via code. See
    http://stackoverflow.com/questions/33675349/spring-data-rest-max-page-size-does-not-seem-to-work
    for details. (Simply specifying a value in application.propeties didn't work.)
 */
  public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;


  /**
   * Returns the pageNumber of the specified pageable.
   */
  public static int getPageNumber(Pageable pageable) {
    if (pageable == null) {
      return DEFAULT_PAGE_NUMBER;
    } else {
      return pageable.getPageNumber();
    }
  }

  /**
   * Returns the pageSize of the specified pageable.
   */
  public static int getPageSize(Pageable pageable) {
    if (pageable == null) {
      return DEFAULT_PAGE_SIZE;
    } else {
      return pageable.getPageSize();
    }
  }

  /**
   * Convenience method for getPage(List originalList, Pageable pageable).
   */
  public static <T> Page<T> getPage(Iterable<T> data, Pageable pageable) {
    List<T> resultList = new ArrayList<T>();
    data.forEach(resultList::add);
    return getPage(resultList, pageable);
  }

  /**
   * Returns the Page for a subset of the specified list, determined by the pageable passed in.
   * @param originalList A list of values, some or all of which should be included in a page.
   * @param pageable An object used to encapsulate the pagination related values: page and size.
   */
  public static <T> Page<T> getPage(List<T> originalList, Pageable pageable) {

    int pageSize = getPageSize(pageable);
    int pageNumber = getPageNumber(pageable);

    int fromIndex = pageNumber * pageSize;
    int toIndex = fromIndex + pageSize;

    //Validate toIndex.
    //Note that toIndex is exclusive, which is why we don't want originalList.size() - 1.
    int maxPossibleToIndex = originalList.size();
    if (toIndex > maxPossibleToIndex) {
      toIndex = maxPossibleToIndex;
    }

    //Validate fromIndex
    int maxPossibleFromIndex = originalList.size() - 1;
    if (fromIndex > maxPossibleFromIndex) {
      // If the fronIndex is out of bounds, set it and toIndex to the same value.
      // This will cause us to return an empty list.
      fromIndex = toIndex = 0;
    }

    List<T> subList = originalList.subList(fromIndex, toIndex);

    Page pageImpl = new PageImpl<T>(subList, pageable, originalList.size());
    return pageImpl;
  }


  /**
   * Returns the Page for the entirety of the specified list. Intended for use when the
   * supplied list already contains exactly the set of elements requested and there's
   * no need to return a subset of it.
   */
  public static <T> Page<T> getPage(List<T> subList, Pageable pageable, long fullListSize) {
    return new PageImpl<T>(subList, pageable, fullListSize);
  }

}
