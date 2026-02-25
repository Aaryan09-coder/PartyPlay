package com.vibey.PartPlay.repo;

import com.vibey.PartPlay.Entity.ProfilePhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepo extends MongoRepository<ProfilePhoto, String> {
}
