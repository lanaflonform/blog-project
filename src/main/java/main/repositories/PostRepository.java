package main.repositories;

import main.DTOEntity.CalendarDto;
import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;

@Transactional
@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer>
{

    @Transactional(readOnly = true)
    @Query(value = "Select distinct p from Post p where p.isActive = :active " +
                    "AND p.moderationStatus = :ms AND p.time < curtime()")
    List<Post> findDistinctByActiveAndModerationStatus(Byte active, ModerationStatus ms, Pageable pageable);

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM posts WHERE is_active = :active AND moderation_status = :ms " +
                   "AND time < curtime() AND :date = Date(time)", nativeQuery = true)
    List<Post> findAllPostsByDate(Byte active, String ms, String date, Pageable pageable);

    @Transactional(readOnly = true)
    @Query(value = "Select p from Post p where p.isActive = :active " +
            "AND p.moderationStatus = :ms AND p.time < curtime() AND p.id = :id")
    Optional<Post> findPostById(Byte active, ModerationStatus ms, Integer id);

    @Transactional(readOnly = true)
    @Query(value = " SELECT DISTINCT p.* FROM posts p " +
            "JOIN tag2post tp ON p.id = tp.post_id " +
            "JOIN tags t ON t.id = tp.tag_id " +
            "WHERE p.is_active = :active AND p.moderation_status = :ms" +
            " AND p.time < curtime() AND :tag = t.name"
            , nativeQuery = true)
    List<Post> findAllPostsByTag(Byte active, String ms, String tag, Pageable paging);

    @Transactional(readOnly = true)
    @Query(value = "Select p from Post p where p.isActive = :active " +
            "AND p.moderationStatus = :ms AND p.time < curtime() AND p.text like %:query% OR p.title like %:query%")
    List<Post> findPostBySearch(Byte active, ModerationStatus ms, String query);

    @Transactional(readOnly = true)
    @Query(value ="select date(p.time), count(p) from Post p where p.time < curtime() and YEAR(p.time) = :year group by p.time")
    List<String> findCountPostForCalendar(Integer year);

    @Transactional(readOnly = true)
    @Query(value="select year(p.time) from Post p WHERE  p.time < curtime() group by year(p.time)")
    List<Integer> findAllYearWithPosts();
}