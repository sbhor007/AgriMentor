package com.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.server.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long>{
	 @Query("SELECT AVG(r.stars) FROM Rating r JOIN r.feedback f WHERE f.consultantId = :cid")
	    Double getAverageByConsultant(@Param("cid") Long consultantId);
}
