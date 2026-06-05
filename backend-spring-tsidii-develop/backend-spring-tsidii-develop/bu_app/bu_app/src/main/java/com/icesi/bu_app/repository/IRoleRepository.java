package com.icesi.bu_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Integer> {
	List<Role> findByIsDeletedFalse();

	Page<Role> findByIsDeletedFalse(Pageable pageable);

	Optional<Role> findByIdAndIsDeletedFalse(Integer id);

	Optional<Role> findByType(String type);

}
