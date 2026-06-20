package ru.tsu_taskgraph.core_api.entity;

import lombok.Getter;

@Getter
public enum AuthorType {
    USER,
    AI,
    SYSTEM;

    public static final String AI_DISPLAY_NAME = "ИИ";
}