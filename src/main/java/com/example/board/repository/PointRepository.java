package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.example.board.model.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
  @Query(nativeQuery = true, 
    value = "select p.*, (6371 * acos(cos(radians(?1)) * cos(radians(p.latitude)) * " +
            "       cos(radians(p.longitude) - radians(?2)) + sin(radians(?3)) * " +
            "       sin(radians(p.latitude)))) as distance " + 
            "  from point p " +
            "having distance <= ?4 " +
            " order by distance")
  public List<Point> findByLatLng(double lat1, double lng, double lat2, int km);
}


// SELECT *,
//        (6371 * ACOS(
//                  cos(radians(현재위도)) 
// 					       * cos(radians(latitude))
//                  * cos(radians(longitude) - radians(현재경도))
//                  + sin(radians(현재위도))
//                  * sin(radians(latitude)))) AS distance
//   FROM point
// HAVING distance <= 10
//  ORDER BY distance 