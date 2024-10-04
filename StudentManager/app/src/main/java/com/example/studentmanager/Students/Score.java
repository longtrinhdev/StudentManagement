package com.example.studentmanager.Students;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Score implements Serializable {
    private float foreign_language;
    private float literature;
    private float math;

    public Score() {
    }

    public Score(float foreign_language, float literature, float math) {
        this.foreign_language = foreign_language;
        this.literature = literature;
        this.math = math;
    }

    public float getForeign_language() {
        return foreign_language;
    }

    public void setForeign_language(float foreign_language) {
        this.foreign_language = foreign_language;
    }

    public float getLiterature() {
        return literature;
    }

    public void setLiterature(float literature) {
        this.literature = literature;
    }

    public float getMath() {
        return math;
    }

    public void setMath(float math) {
        this.math = math;
    }

    public Map<String, Object> mapScore() {
        Map<String, Object> map = new HashMap<>();
        map.put("math", getMath());
        map.put("literature", getLiterature());
        map.put("foreign_language", getForeign_language());

        return map;
    }
}
