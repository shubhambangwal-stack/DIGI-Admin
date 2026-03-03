package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByParentIsNull();

    List<ForumPost> findByParentId(Long parentId);

    List<ForumPost> findByBuilding(Building building);
}
