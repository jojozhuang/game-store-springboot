/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.paging;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.Serializable;
import java.util.List;

public class PagedResource<T> implements Serializable {
  private transient List<T> data;

  private Pagination pagination;

  public PagedResource(
      PagedResourcesAssembler<T> assembler,
      WebMvcLinkBuilder linkBuilder,
      Page<T> page,
      String host) {
    this.data = page.getContent();

    String uri =
        UriComponentsBuilder.fromHttpUrl(host)
            .path(linkBuilder.toUri().getPath())
            .query(linkBuilder.toUri().getQuery())
            .toUriString();

    PagedModel<EntityModel<T>> resources = assembler.toModel(page, Link.of(uri));
    this.pagination =
        Pagination.builder()
            .previous(getPreviousLink(resources))
            .next(getNextLink(resources))
            .totalCount(page.getTotalElements())
            .build();
  }

  private String getPreviousLink(PagedModel<EntityModel<T>> pagedResources) {
    return pagedResources.getPreviousLink().map(Link::getHref).orElse(null);
  }

  private String getNextLink(PagedModel<EntityModel<T>> pagedResources) {
    return pagedResources.getNextLink().map(Link::getHref).orElse(null);
  }

  public List<T> getData() {
    return data;
  }

  public Pagination getPagination() {
    return pagination;
  }
}
