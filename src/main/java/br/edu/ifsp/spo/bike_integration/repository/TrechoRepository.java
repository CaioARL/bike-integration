package br.edu.ifsp.spo.bike_integration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifsp.spo.bike_integration.model.Trecho;

public interface TrechoRepository extends JpaRepository<Trecho, Long> {

	Optional<Trecho> findById(Long id);

	List<Trecho> findAll();

	@Query(value = "SELECT * FROM trecho WHERE id_infraestrutura_cicloviaria = :id", nativeQuery = true)
	List<Trecho> findByInfraestruturaCicloviariaId(Long id);

	@Query(value = "SELECT t.* FROM trecho t "
			+ "JOIN infraestrutura_cicloviaria e ON e.id = t.id_infraestrutura_cicloviaria "
			+ "WHERE ST_Distance_Sphere(POINT(t.latitude, t.longitude), POINT(:latitude, :longitude)) <= :raio", nativeQuery = true)
	List<Trecho> findTrechosProximosByLocation(@Param("latitude") double latitude, @Param("longitude") double longitude,
			@Param("raio") double raio);

	@Query(value = "SELECT t.* FROM trecho t "
			+ "JOIN infraestrutura_cicloviaria e ON e.id = t.id_infraestrutura_cicloviaria "
			+ "WHERE ST_Distance_Sphere(POINT(t.latitude, t.longitude), POINT(:latitude, :longitude)) <= 100 "
			+ "ORDER BY ST_Distance_Sphere(POINT(t.latitude, t.longitude), POINT(:latitude, :longitude)) ASC "
			+ "LIMIT 1", nativeQuery = true)
	Trecho findTrechoProximoByLocation(@Param("latitude") double latitude, @Param("longitude") double longitude);

}
