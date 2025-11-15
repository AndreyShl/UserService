package org.example.model.repository;

import org.example.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

// - JPA provides
// - Crud
// - findById(ID id)
@Repository
public interface UsersRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {


}
