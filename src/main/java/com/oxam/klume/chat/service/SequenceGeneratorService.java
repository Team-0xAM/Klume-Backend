package com.oxam.klume.chat.service;

import com.oxam.klume.chat.document.Sequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public int getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        Sequence counter = mongoOperations.findAndModify(
            query,
            update,
            options().returnNew(true).upsert(true),
            Sequence.class
        );
        return counter != null ? counter.getSeq() : 1;
    }
}
