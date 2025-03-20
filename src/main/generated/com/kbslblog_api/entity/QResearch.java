package com.kbslblog_api.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QResearch is a Querydsl query type for Research
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResearch extends EntityPathBase<Research> {

    private static final long serialVersionUID = -1947575630L;

    public static final QResearch research = new QResearch("research");

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath subTitle = createString("subTitle");

    public final StringPath title = createString("title");

    public final StringPath url = createString("url");

    public QResearch(String variable) {
        super(Research.class, forVariable(variable));
    }

    public QResearch(Path<? extends Research> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResearch(PathMetadata metadata) {
        super(Research.class, metadata);
    }

}

