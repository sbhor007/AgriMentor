package com.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.server.entity.Rating;

@Repository
@EnableJpaRepositories
public interface RatingRepository extends JpaRepository<Rating,Long>{
  // Correct JPQL: navigate through feedback -> consultant -> id
//	    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.feedback.consultant.id = :cid")
//	    Double getAverageByConsultantId(@Param("cid") Long consultantId);
////
//	 Double getAverageByConsultantId(Long consultantId);
	Double findAverageByFeedback_Consultant_Id(Long consultantId);
 
}
