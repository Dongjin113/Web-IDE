package com.ogjg.back.file.domain;

import com.ogjg.back.container.domain.Container;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class File {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String uuid;

    @ManyToOne
    @JoinColumn(name = "containerId")
    private Container container;

    private String name;

    private String path;

    @Builder
    public File(String uuid, Container container, String name, String path) {
        this.uuid = uuid;
        this.container = container;
        this.name = name;
        this.path = path;
    }

    public File rename(String newFilename) {
        this.name = newFilename;
        return this;
    }
}