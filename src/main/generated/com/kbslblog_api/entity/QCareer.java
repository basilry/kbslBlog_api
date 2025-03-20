package com.kbslblog_api.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCareer is a Querydsl query type for Career
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCareer extends EntityPathBase<Career> {

    private static final long serialVersionUID = -654181803L;

    public static final QCareer career = new QCareer("career");

    public final StringPath endDate = createString("endDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath startDate = createString("startDate");

    public final StringPath title = createString("title");

    public QCareer(String variable) {
        super(Career.class, forVariable(variable));
    }

    public QCareer(Path<? extends Career> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCareer(PathMetadata metadata) {
        super(Career.class, metadata);
    }

}

