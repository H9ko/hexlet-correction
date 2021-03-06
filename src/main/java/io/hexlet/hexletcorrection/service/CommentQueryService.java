package io.hexlet.hexletcorrection.service;

import io.github.jhipster.service.QueryService;
import io.hexlet.hexletcorrection.domain.Comment;
import io.hexlet.hexletcorrection.domain.Comment_;
import io.hexlet.hexletcorrection.domain.Correction_;
import io.hexlet.hexletcorrection.domain.Preference_;
import io.hexlet.hexletcorrection.repository.CommentRepository;
import io.hexlet.hexletcorrection.service.dto.CommentCriteria;
import io.hexlet.hexletcorrection.service.dto.CommentDTO;
import io.hexlet.hexletcorrection.service.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for {@link Comment} entities in the database.
 * The main input is a {@link CommentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommentDTO} or a {@link Page} of {@link CommentDTO} which fulfills the criteria.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentQueryService extends QueryService<Comment> {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    /**
     * Return a {@link List} of {@link CommentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommentDTO> findByCriteria(CommentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentMapper.toDto(commentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommentDTO> findByCriteria(CommentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.findAll(specification, page)
            .map(commentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.count(specification);
    }

    /**
     * Function to convert {@link CommentCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Comment> createSpecification(CommentCriteria criteria) {
        Specification<Comment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Comment_.id));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Comment_.message));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Comment_.date));
            }
            if (criteria.getAuthorId() != null) {
                specification = specification.and(buildSpecification(criteria.getAuthorId(),
                    root -> root.join(Comment_.author, JoinType.LEFT).get(Preference_.id)));
            }
            if (criteria.getCorrectionId() != null) {
                specification = specification.and(buildSpecification(criteria.getCorrectionId(),
                    root -> root.join(Comment_.correction, JoinType.LEFT).get(Correction_.id)));
            }
        }
        return specification;
    }
}
