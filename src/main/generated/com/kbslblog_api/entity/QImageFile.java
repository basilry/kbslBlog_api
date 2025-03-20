package com.kbslblog_api.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QImageFile is a Querydsl query type for ImageFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImageFile extends EntityPathBase<ImageFile> {

    private static final long serialVersionUID = 1163949888L;

    public static final QImageFile imageFile = new QImageFile("imageFile");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final StringPath hash = createString("hash");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mimeType = createString("mimeType");

    public final StringPath url = createString("url");

    public QImageFile(String variable) {
        super(ImageFile.class, forVariable(variable));
    }

    public QImageFile(Path<? extends ImageFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QImageFile(PathMetadata metadata) {
        super(ImageFile.class, metadata);
    }

}

