package com.ptc.halo.dtoRequest;

import lombok.Data;

@Data
public class LessonGenerationRequest {

    private Integer week;

    private String lessonText;

    private String aiNotes;
}