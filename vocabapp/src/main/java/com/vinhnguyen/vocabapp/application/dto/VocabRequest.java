package com.vinhnguyen.vocabapp.application.dto;

import lombok.Data;

@Data
public class VocabRequest {
    private String word;
    private String meaning;
    private String partOfSpeech;
    private String exampleSentence;
}